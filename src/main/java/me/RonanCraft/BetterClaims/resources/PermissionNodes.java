package me.RonanCraft.BetterClaims.resources;

import me.RonanCraft.BetterClaims.BetterClaims;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public enum PermissionNodes {

    //betterclaims.<node>
    USE("use"),
    RELOAD("reload"),
    REQUEST("request"),
    UPDATE("update"),
    CLAIM_ITEM("item"),
    ADMIN_CLAIM("admin.claim"),
    ADMIN_CONVERT("admin.convert"),
    ADMIN_OVERRIDE("admin.override"),
    CLAIM_WORLD("world.%world%"),
    TELEPORT("teleport"),
    ;

    public final String node;

    PermissionNodes(String node) {
        this.node = "betterclaims." + node;
    }

    public boolean check(CommandSender sendi) {
        return check(sendi, this.node);
    }

    public boolean check(CommandSender sendi, World world) {
        return check(sendi, this.node.replace("%world%", world.getName()));
    }

    private boolean check(CommandSender sendi, String node) {
        return BetterClaims.getInstance().getPermissions().checkPerm(node, sendi);
    }
}
