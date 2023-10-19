package me.RonanCraft.BetterClaims.resources;

import me.RonanCraft.BetterClaims.resources.dependencies.DepPermissions;
import org.bukkit.command.CommandSender;

public class Permissions {
    private final DepPermissions depPerms = new DepPermissions();

    public void register() {
        depPerms.register();
    }

    public boolean checkPerm(String str, CommandSender sendi) {
        return depPerms.hasPerm(str, sendi);
    }
}
