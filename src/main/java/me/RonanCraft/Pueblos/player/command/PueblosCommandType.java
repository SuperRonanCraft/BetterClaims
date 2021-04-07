package me.RonanCraft.Pueblos.player.command;

import me.RonanCraft.Pueblos.player.command.types.*;

public enum PueblosCommandType {
    CREATE(new  CmdCreate()),
    RELOAD(new  CmdReload()),
    HELP(new    CmdHelp()),
    INFO(new    CmdInfo());

    private final PueblosCommand cmd;

    PueblosCommandType(PueblosCommand cmd) {
        this.cmd = cmd;
    }

    public PueblosCommand getCmd() {
        return cmd;
    }
}
