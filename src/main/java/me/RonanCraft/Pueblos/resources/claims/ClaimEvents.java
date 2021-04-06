package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.claims.events.ClaimEvent_Create;
import org.bukkit.Bukkit;

public class ClaimEvents {

    public static void create(Claim c) {
        Bukkit.getServer().getPluginManager().callEvent(new ClaimEvent_Create(c));
    }
}
