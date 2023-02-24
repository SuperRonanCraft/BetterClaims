package me.RonanCraft.BetterClaims.player.command;

import me.RonanCraft.BetterClaims.player.command.types.*;

public enum PueblosCommandType {
    CREATE(new          CmdCreate()),
    RELOAD(new          CmdReload()),
    HELP(new            CmdHelp()),
    INFO(new            CmdInfo()),
    LIST(new            CmdList()),
    FLAG(new            CmdFlags()),
    REQUEST(new         CmdRequest()),
    CONVERT(new         CmdConvert()),
    ADMIN_CLAIM(new CmdAdminClaim()),
    ADMIN_OVERRIDE(new  CmdAdminOverride()),
    //CLAIMITEM(new       CmdClaimItem()),
    ADDMEMBER(new       CmdAddMember()),
    ;

    private final PueblosCommand cmd;

    PueblosCommandType(PueblosCommand cmd) {
        this.cmd = cmd;
    }

    public PueblosCommand getCmd() {
        return cmd;
    }
}
