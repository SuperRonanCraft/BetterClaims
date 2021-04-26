package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.customevents.*;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class HelperEvent {

    public static Cancellable claimCreate(Claim claim, Player creator) {
        Event event = new PueblosEvent_ClaimCreate(claim, creator);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable claimDelete(Claim claim) {
        Event event = new PueblosEvent_ClaimDelete(claim);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable claimResize(ClaimInfo claim, Player editor, Location loc_1, Location loc_2) {
        Event event = new PueblosEvent_ClaimResize(claim, editor, loc_1, loc_2);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable memberLeave(ClaimMember member) {
        Event event = new PueblosEvent_MemberLeave(member);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable teleportToClaim(Claim claim, Player player, Location from) {
        Event event = new PueblosEvent_ClaimTeleportTo(claim, player, from);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }
}
