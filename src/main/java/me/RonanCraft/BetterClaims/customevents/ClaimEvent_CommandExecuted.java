package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.player.command.ClaimCommand;
import org.bukkit.command.CommandSender;

//Called when a `/claim <cmd>` command was executed
public class ClaimEvent_CommandExecuted extends ClaimEventType_Basic {

    private final ClaimCommand cmd;
    private final CommandSender sender;

    public ClaimEvent_CommandExecuted(CommandSender sender, ClaimCommand cmd) {
        this.cmd = cmd;
        this.sender = sender;
    }

    public ClaimCommand getCommand() {
        return cmd;
    }

    public CommandSender getSender() {
        return sender;
    }
}
