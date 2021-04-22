package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.Pueblos;
import org.bukkit.command.CommandSender;

public enum PermissionNodes {

    USE("use"),
    RELOAD("reload"),
    REQUEST("request"),
    UPDATE("update"),
    ADMIN_CLAIM("admin.claim"),
    ADMIN_OVERRIDE("admin.override");

    public String node;

    PermissionNodes(String node) {
        this.node = "pueblos." + node;
    }

    public boolean check(CommandSender sendi) {
        return Pueblos.getInstance().getPermissions().checkPerm(node, sendi);
    }
}
