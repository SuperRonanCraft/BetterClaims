package me.RonanCraft.Pueblos.player.command;

import me.RonanCraft.Pueblos.player.command.types.*;

public enum PueblosCommandType {
    CREATE(new      CmdCreate()),
    RELOAD(new      CmdReload()),
    HELP(new        CmdHelp()),
    INFO(new        CmdInfo()),
    LIST(new        CmdList()),
    FLAG(new        CmdFlags()),
    REQUEST(new     CmdRequest()),
    CONVERT(new     CmdConvert()),
    ADMINCLAIM(new  CmdAdminclaim());

    private final PueblosCommand cmd;

    PueblosCommandType(PueblosCommand cmd) {
        this.cmd = cmd;
    }

    public PueblosCommand getCmd() {
        return cmd;
    }
}
