package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;

//Called when a `/pueblos <cmd>` command was executed
public class PueblosEvent_CommandExecuted extends PueblosEventType_Basic {

    private final PueblosCommand cmd;

    public PueblosEvent_CommandExecuted(PueblosCommand cmd) {
        this.cmd = cmd;
    }

    public PueblosCommand getCommand() {
        return cmd;
    }
}
