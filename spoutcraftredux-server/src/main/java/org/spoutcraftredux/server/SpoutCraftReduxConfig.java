package org.spoutcraftredux.server;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/** spoutCraftRedux.yml in the server root. */
public final class SpoutCraftReduxConfig {
    private static final String HEADER = """
        Main configuration file for SpoutCraft Redux.
        Website: https://spoutcraftredux.org
        """;

    public static int handshakeTimeoutTicks = 40;
    public static boolean skipHandshakeForVanillaBrand = true;

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
        config.setInlineComments("handshake.timeout-ticks", java.util.List.of(
            "How long to wait for the SpoutCraft Redux client to answer while a player connects. 20 ticks = 1 second."));
        config.addDefault("handshake.skip-for-vanilla-brand", true);
        config.setInlineComments("handshake.skip-for-vanilla-brand", java.util.List.of(
            "Skip the wait for clients that report the \"vanilla\" brand."));

        handshakeTimeoutTicks = Math.max(1, config.getInt("handshake.timeout-ticks"));
        skipHandshakeForVanillaBrand = config.getBoolean("handshake.skip-for-vanilla-brand");

        config.save(file);
    }
}
