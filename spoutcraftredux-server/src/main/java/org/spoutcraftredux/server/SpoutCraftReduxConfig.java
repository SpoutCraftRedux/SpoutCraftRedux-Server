package org.spoutcraftredux.server;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/** spoutCraftRedux.yml in the server root. */
public final class SpoutCraftReduxConfig {
    public static int handshakeTimeoutTicks = 40;
    public static boolean skipHandshakeForVanillaBrand = true;
    public static int resourcePackPort = 25580;
    public static String resourcePackPublicUrl = "";
    public static boolean resourcePackRequired;
    public static String resourcePackPrompt = "";
    public static String missingClientMessage = "";

    private static final String DEFAULT_MISSING_CLIENT_MESSAGE = "<gold>This server uses <yellow>SpoutCraft Redux</yellow>. "
        + "Get the client at <click:open_url:'https://spoutcraftredux.org/downloads'><aqua><underlined>spoutcraftredux.org</underlined></aqua></click>"
        + " for the full experience.";
    private static final String HEADER = """
        Main configuration file for SpoutCraft Redux.
        Website: https://spoutcraftredux.org
        """;

    private SpoutCraftReduxConfig() {
    }

    static void load(final File file) throws IOException, InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        if (file.exists()) {
            config.load(file);
        }
        config.options().setHeader(HEADER.lines().toList());
        config.options().copyDefaults(true);

        config.addDefault("handshake.timeout-ticks", 40);
        config.setInlineComments("handshake.timeout-ticks", List.of(
            "How long to wait for the SpoutCraft Redux client to answer while a player connects. 20 ticks = 1 second."));
        config.addDefault("handshake.skip-for-vanilla-brand", true);
        config.setInlineComments("handshake.skip-for-vanilla-brand", List.of(
            "Skip the wait for clients that report the \"vanilla\" brand."));

        handshakeTimeoutTicks = Math.max(1, config.getInt("handshake.timeout-ticks"));
        skipHandshakeForVanillaBrand = config.getBoolean("handshake.skip-for-vanilla-brand");

        config.addDefault("resource-pack.port", 25580);
        config.setInlineComments("resource-pack.port", List.of("Port of the built-in web server that hands out the merged plugin resource pack."));
        config.addDefault("resource-pack.public-url", "");
        config.setInlineComments("resource-pack.public-url", List.of(
            "Set this if players reach the pack through a proxy or another host, like https://pack.example.com. Empty uses the address players joined with."));
        config.addDefault("resource-pack.required", false);
        config.setInlineComments("resource-pack.required", List.of("Kick players who decline the pack."));
        config.addDefault("resource-pack.prompt", "");
        config.setInlineComments("resource-pack.prompt", List.of("Message shown when asking players to accept the pack. Empty uses Minecraft's default."));
        config.addDefault("missing-client.message", DEFAULT_MISSING_CLIENT_MESSAGE);
        config.setInlineComments("missing-client.message", List.of(
            "Chat message for players who join without the SpoutCraft Redux client, in MiniMessage format. Empty sends nothing."));
        missingClientMessage = config.getString("missing-client.message", "");

        resourcePackPort = config.getInt("resource-pack.port");
        resourcePackPublicUrl = config.getString("resource-pack.public-url", "");
        resourcePackRequired = config.getBoolean("resource-pack.required");
        resourcePackPrompt = config.getString("resource-pack.prompt", "");

        config.save(file);
    }
}
