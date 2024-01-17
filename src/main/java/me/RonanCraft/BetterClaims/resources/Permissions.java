package me.RonanCraft.BetterClaims.resources;

import me.RonanCraft.BetterClaims.resources.dependencies.DepPermissions;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permissions {
    private final DepPermissions depPerms = new DepPermissions();

    public void register() {
        depPerms.register();
    }

    public boolean checkPerm(String str, CommandSender sendi) {
        return depPerms.hasPerm(str, sendi);
    }

    public static boolean canCreateClaimIn(Player player, World world) {
        return PermissionNodes.CLAIM_WORLD.check(player, world);
    }

    public static boolean getMaxClaimSize(Player player) {
        return false;
    }
}
