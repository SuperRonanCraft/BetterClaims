package me.RonanCraft.BetterClaims.resources.dependencies;

import me.RonanCraft.BetterClaims.BetterClaims;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DepPermissions {
    public Permission p = null;

    public boolean hasPerm(String perm, CommandSender sendi) {
        //sendi.sendMessage(perm);
        if (p != null)
            return p.has(sendi, perm);
        return sendi.hasPermission(perm);
    }

    public void register() {
        try {
            if (BetterClaims.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {
                RegisteredServiceProvider<Permission> permissionProvider = BetterClaims.getInstance().getServer()
                        .getServicesManager().getRegistration(Permission.class);
                p = permissionProvider.getProvider();
            } else
                p = null;
        } catch (NullPointerException e) {
            //Vault but no Perms
        }
    }
}
