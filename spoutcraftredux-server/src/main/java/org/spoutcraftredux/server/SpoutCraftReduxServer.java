package org.spoutcraftredux.server;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.Set;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.spoutcraftredux.protocol.Capabilities;
import org.spoutcraftredux.server.command.SpoutCraftReduxCommand;

/** Entry point called from the server bootstrap. */
public final class SpoutCraftReduxServer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final File CONFIG_FILE = new File("spoutcraftredux.yml");
    /** Features this server build implements; grows as phases land. */
    public static final Set<String> SERVER_CAPABILITIES = Set.of(Capabilities.NOTIFICATION, Capabilities.KEYBIND, Capabilities.UI, Capabilities.HUD, Capabilities.ASSET, Capabilities.CONTENT);

    private SpoutCraftReduxServer() {
    }

    public static boolean init(final MinecraftServer server) {
        try {
            SpoutCraftReduxConfig.load(CONFIG_FILE);
        } catch (Exception e) {
            LOGGER.error("Could not load {}, please fix its syntax", CONFIG_FILE, e);
            return false;
        }
        registerCommands(server);
        return true;
    }

    public static void registerCommands(final MinecraftServer server) {
        server.server.getCommandMap().register("spoutcraftredux", "SpoutCraftRedux", new SpoutCraftReduxCommand("spoutcraftredux"));
    }
}
