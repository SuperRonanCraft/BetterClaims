package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import org.bukkit.command.CommandSender;

//Called when a `/pueblos <cmd>` command was executed
public class PueblosEvent_CommandExecuted extends PueblosEventType_Basic {

    private final PueblosCommand cmd;
    private final CommandSender sender;

    public PueblosEvent_CommandExecuted(CommandSender sender, PueblosCommand cmd) {
        this.cmd = cmd;
        this.sender = sender;
    }

    public PueblosCommand getCommand() {
        return cmd;
    }

    public CommandSender getSender() {
        return sender;
    }
}
