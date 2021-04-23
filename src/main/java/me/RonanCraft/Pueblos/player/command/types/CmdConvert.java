package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandEnableable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.dependencies.ConverterGriefPrevention;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesHelp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdConvert implements PueblosCommand, PueblosCommandEnableable {

    public String getName() {
        return "convert";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        new ConverterGriefPrevention().load(p);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.ADMIN_CLAIM.check(sendi);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ConverterGriefPrevention.pathExist();
    }
}
