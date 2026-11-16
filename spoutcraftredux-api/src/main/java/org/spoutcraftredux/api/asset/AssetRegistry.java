package org.spoutcraftredux.api.asset;

import java.io.IOException;
import java.nio.file.Path;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

/**
 * Images for widgets and files for the server resource pack.
 * <p>
 * Images are PNGs of at most 4 MiB, shown with {@link org.spoutcraftredux.api.gui.Texture}. Players with the
 * SpoutCraft Redux client download each one the first time a widget uses it.
 * <p>
 * Resource pack files (models, textures, sounds, fonts, languages) from every plugin are merged into one pack, which
 * is sent to every player when they join, with or without the client.
 */
public interface AssetRegistry {

    /**
     * Registers an image from bytes.
     *
     * @param plugin the plugin that owns it
     * @param id     its id, such as {@code myplugin:logo}
     * @param png    the PNG file
     * @return the asset
     * @throws IllegalArgumentException if it isn't a PNG or is larger than 4 MiB
     */
    Asset registerImage(Plugin plugin, Key id, byte[] png);

    /**
     * Registers an image from a file inside the plugin's jar.
     *
     * @param plugin the plugin that owns it
     * @param id     its id, such as {@code myplugin:logo}
     * @param path   the path inside the jar, such as {@code images/logo.png}
     * @return the asset
     * @throws IllegalArgumentException if the file is missing, isn't a PNG or is larger than 4 MiB
     */
    Asset registerImage(Plugin plugin, Key id, String path);

    /**
     * Gets a registered image.
     *
     * @param id the id
     * @return the asset, or null if none is registered
     */
    @Nullable Asset get(Key id);

    /**
     * Adds a folder inside the plugin's jar to the server resource pack. The folder is laid out like a resource
     * pack: {@code assets/<namespace>/...}. A {@code pack.mcmeta} in it is ignored; the server writes its own.
     *
     * @param plugin the plugin
     * @param folder the folder inside the jar, such as {@code resourcepack}
     * @throws IOException if the jar can't be read
     */
    void addResourcePack(Plugin plugin, String folder) throws IOException;

    /**
     * Adds a resource pack zip or folder on disk to the server resource pack. Files from plugins added later
     * replace files with the same path from plugins added earlier.
     *
     * @param plugin the plugin
     * @param pack   the zip file or folder
     * @throws IOException if it can't be read
     */
    void addResourcePack(Plugin plugin, Path pack) throws IOException;
}
