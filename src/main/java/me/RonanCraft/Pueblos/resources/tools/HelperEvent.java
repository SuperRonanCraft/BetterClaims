package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.player.events.custom.EventClaimCreate;
import me.RonanCraft.Pueblos.player.events.custom.EventClaimResize;
import me.RonanCraft.Pueblos.player.events.custom.EventClaimTeleportTo;
import me.RonanCraft.Pueblos.player.events.custom.EventMemberLeave;
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
        Event event = new EventClaimCreate(claim, creator);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable claimResize(ClaimInfo claim, Player editor, Location loc_1, Location loc_2) {
        Event event = new EventClaimResize(claim, editor, loc_1, loc_2);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable memberLeave(ClaimMember member) {
        Event event = new EventMemberLeave(member);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }

    public static Cancellable teleportToClaim(Claim claim, Player player, Location from) {
        Event event = new EventClaimTeleportTo(claim, player, from);
        Bukkit.getPluginManager().callEvent(event);
        return (Cancellable) event;
    }
}
