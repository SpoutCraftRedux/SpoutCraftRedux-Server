package org.spoutcraftredux.server.asset;

import com.mojang.logging.LogUtils;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spoutcraftredux.api.asset.Asset;
import org.spoutcraftredux.api.asset.AssetRegistry;
import org.spoutcraftredux.protocol.Capabilities;
import org.spoutcraftredux.protocol.message.AssetData;
import org.spoutcraftredux.server.SpoutCraftReduxConfig;
import org.spoutcraftredux.server.network.Payloads;

public final class CraftAssetRegistry implements AssetRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};

    private final Map<Key, Asset> assets = new LinkedHashMap<>();
    private final Map<String, byte[]> images = new LinkedHashMap<>();
    private final Map<String, byte[]> packFiles = new LinkedHashMap<>();
    private byte @Nullable [] pack;
    private @Nullable String packSha1;
    private @Nullable HttpServer http;

    @Override
    public synchronized Asset registerImage(final Plugin plugin, final Key id, final byte[] png) {
        if (png.length > AssetData.MAX_ASSET_BYTES) {
            throw new IllegalArgumentException("Images can be at most 4 MiB, " + id + " is " + png.length + " bytes");
        }
        if (png.length < PNG_SIGNATURE.length || !Arrays.equals(Arrays.copyOf(png, PNG_SIGNATURE.length), PNG_SIGNATURE)) {
            throw new IllegalArgumentException(id + " is not a PNG file");
        }
        final String sha256 = hex("SHA-256", png);
        this.images.put(sha256, png.clone());
        final Asset asset = new Asset(id, sha256, png.length);
        this.assets.put(id, asset);
        return asset;
    }

    @Override
    public Asset registerImage(final Plugin plugin, final Key id, final String path) {
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) {
                throw new IllegalArgumentException(plugin.getName() + " has no file " + path);
            }
            return this.registerImage(plugin, id, in.readAllBytes());
        } catch (final IOException e) {
            throw new IllegalArgumentException("Could not read " + path + " from " + plugin.getName(), e);
        }
    }

    @Override
    public synchronized @Nullable Asset get(final Key id) {
        return this.assets.get(id);
    }

    @Override
    public synchronized void addResourcePack(final Plugin plugin, final String folder) throws IOException {
        final String prefix = folder.endsWith("/") ? folder : folder + "/";
        final URL location = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
        try (JarFile jar = new JarFile(Path.of(URI.create(location.toString())).toFile())) {
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(prefix)) {
                    try (InputStream in = jar.getInputStream(entry)) {
                        this.addPackFile(entry.getName().substring(prefix.length()), in.readAllBytes());
                    }
                }
            }
        }
        this.pack = null;
    }

    @Override
    public synchronized void addResourcePack(final Plugin plugin, final Path pack) throws IOException {
        if (Files.isDirectory(pack)) {
            try (Stream<Path> files = Files.walk(pack)) {
                for (final Path file : files.filter(Files::isRegularFile).toList()) {
                    this.addPackFile(pack.relativize(file).toString().replace('\\', '/'), Files.readAllBytes(file));
                }
            }
        } else {
            try (ZipFile zip = new ZipFile(pack.toFile())) {
                final Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        try (InputStream in = zip.getInputStream(entry)) {
                            this.addPackFile(entry.getName(), in.readAllBytes());
                        }
                    }
                }
            }
        }
        this.pack = null;
    }

    private void addPackFile(final String path, final byte[] bytes) {
        // Only pack contents; the server writes its own pack.mcmeta, and nothing may escape the pack
        if (path.equals("pack.mcmeta") || path.contains("..") || path.startsWith("/")) {
            return;
        }
        this.packFiles.put(path, bytes);
    }

    /**
     * Sends a registered image to a player, in pieces. Unknown hashes are ignored.
     *
     * @param player the player
     * @param sha256 the image's hash
     */
    public void sendImage(final ServerPlayer player, final String sha256) {
        final byte[] image;
        synchronized (this) {
            image = this.images.get(sha256);
        }
        if (image == null || !player.getBukkitEntity().spoutCraftRedux().hasCapability(Capabilities.ASSET)) {
            return;
        }
        for (int offset = 0; offset < image.length; offset += AssetData.MAX_CHUNK_BYTES) {
            final byte[] piece = Arrays.copyOfRange(image, offset, Math.min(image.length, offset + AssetData.MAX_CHUNK_BYTES));
            Payloads.send(player, AssetData.CHANNEL, new AssetData(sha256, image.length, offset, piece).encode());
        }
    }

    /**
     * Sends the server resource pack to a player who just joined, if any plugin added files to it.
     *
     * @param player the player
     */
    public void sendPack(final Player player) {
        final String sha1;
        synchronized (this) {
            if (this.packFiles.isEmpty()) {
                return;
            }
            this.buildPack();
            sha1 = this.packSha1;
        }
        if (sha1 == null || !this.startHttp()) {
            return;
        }
        final URI uri = URI.create(this.baseUrl(player) + "/pack/" + sha1 + ".zip");
        final ResourcePackRequest.Builder request = ResourcePackRequest.resourcePackRequest()
            .packs(ResourcePackInfo.resourcePackInfo(UUID.nameUUIDFromBytes(("spoutcraftredux:" + sha1).getBytes(StandardCharsets.UTF_8)), uri, sha1))
            .replace(false)
            .required(SpoutCraftReduxConfig.resourcePackRequired);
        if (!SpoutCraftReduxConfig.resourcePackPrompt.isEmpty()) {
            request.prompt(Component.text(SpoutCraftReduxConfig.resourcePackPrompt));
        }
        player.sendResourcePacks(request.build());
    }

    private String baseUrl(final Player player) {
        if (!SpoutCraftReduxConfig.resourcePackPublicUrl.isEmpty()) {
            return SpoutCraftReduxConfig.resourcePackPublicUrl.replaceAll("/+$", "");
        }
        final InetSocketAddress host = player.getVirtualHost();
        final String name = host == null ? "localhost" : host.getHostString();
        return "http://" + name + ":" + SpoutCraftReduxConfig.resourcePackPort;
    }

    private void buildPack() {
        if (this.pack != null) {
            return;
        }
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry("pack.mcmeta"));
            zip.write("""
                {"pack": {"description": "SpoutCraft Redux server pack", "min_format": 97, "max_format": 97}}
                """.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            for (final Map.Entry<String, byte[]> file : this.packFiles.entrySet()) {
                final ZipEntry entry = new ZipEntry(file.getKey());
                // Fixed timestamps keep the hash the same between restarts when nothing changed
                entry.setTime(0L);
                zip.putNextEntry(entry);
                zip.write(file.getValue());
                zip.closeEntry();
            }
        } catch (final IOException e) {
            LOGGER.error("Could not build the resource pack", e);
            return;
        }
        this.pack = bytes.toByteArray();
        this.packSha1 = hex("SHA-1", this.pack);
        LOGGER.info("Built the server resource pack: {} files, {} KiB", this.packFiles.size() + 1, this.pack.length / 1024);
    }

    private synchronized boolean startHttp() {
        if (this.http != null) {
            return true;
        }
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(SpoutCraftReduxConfig.resourcePackPort), 0);
            server.createContext("/pack/", exchange -> {
                final byte[] body;
                synchronized (CraftAssetRegistry.this) {
                    final boolean current = this.packSha1 != null
                        && exchange.getRequestURI().getPath().equals("/pack/" + this.packSha1 + ".zip");
                    body = current ? this.pack : null;
                }
                if (body == null || !exchange.getRequestMethod().equals("GET")) {
                    exchange.sendResponseHeaders(404, -1);
                } else {
                    exchange.getResponseHeaders().set("Content-Type", "application/zip");
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                }
                exchange.close();
            });
            server.setExecutor(Executors.newFixedThreadPool(2, runnable -> {
                final Thread thread = new Thread(runnable, "SpoutCraft Redux resource pack");
                thread.setDaemon(true);
                return thread;
            }));
            server.start();
            this.http = server;
            LOGGER.info("Serving the resource pack on port {}", SpoutCraftReduxConfig.resourcePackPort);
            return true;
        } catch (final IOException e) {
            LOGGER.error("Could not serve the resource pack on port {}", SpoutCraftReduxConfig.resourcePackPort, e);
            return false;
        }
    }

    private static String hex(final String algorithm, final byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance(algorithm).digest(data));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
