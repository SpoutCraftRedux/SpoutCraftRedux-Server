package org.spoutcraftredux.server.command;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;
import org.spoutcraftredux.protocol.SpoutCraftReduxProtocol;

public final class SpoutCraftReduxCommand extends Command {
    public SpoutCraftReduxCommand(final String name) {
        super(name);
        this.description = "SpoutCraft Redux commands";
        this.usageMessage = "/spoutcraftredux [list]";
        this.setPermission("spoutcraftredux.command.spoutcraftredux");
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) {
        return args.length == 1 && "list".startsWith(args[0].toLowerCase()) ? List.of("list") : List.of();
    }

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length > 1 || (args.length == 1 && !args[0].equalsIgnoreCase("list"))) {
            sender.sendMessage(Component.text("Usage: " + this.usageMessage, NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("SpoutCraft Redux, protocol " + SpoutCraftReduxProtocol.VERSION, NamedTextColor.GOLD));
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final SpoutCraftReduxPlayer spoutCraftRedux = player.spoutCraftRedux();
            final Component status = spoutCraftRedux.isModded()
                ? Component.text("SpoutCraft Redux " + spoutCraftRedux.modVersion().orElse("?") + " (protocol " + spoutCraftRedux.protocolVersion() + ")", NamedTextColor.GREEN)
                : Component.text("vanilla", NamedTextColor.GRAY);
            sender.sendMessage(Component.text(" " + player.getName() + ": ", NamedTextColor.WHITE).append(status));
        }
        return true;
    }
}
