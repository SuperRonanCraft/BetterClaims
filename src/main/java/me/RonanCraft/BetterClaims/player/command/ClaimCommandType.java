package me.RonanCraft.BetterClaims.player.command;

import me.RonanCraft.BetterClaims.player.command.types.*;

public enum ClaimCommandType {
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

    private final ClaimCommand cmd;

    ClaimCommandType(ClaimCommand cmd) {
        this.cmd = cmd;
    }

    public ClaimCommand getCmd() {
        return cmd;
    }
}
