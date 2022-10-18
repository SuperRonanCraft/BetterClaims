package me.RonanCraft.Pueblos.resources;

import org.bukkit.command.CommandSender;

public class Permissions {
    private final me.RonanCraft.Pueblos.resources.dependencies.DepPermissions depPerms = new me.RonanCraft.Pueblos.resources.dependencies.DepPermissions();

    public void register() {
        depPerms.register();
    }

    public boolean checkPerm(String str, CommandSender sendi) {
        return depPerms.hasPerm(str, sendi);
    }
}
