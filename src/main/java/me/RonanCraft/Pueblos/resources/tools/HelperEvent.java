package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.customevents.*;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class HelperEvent {

    public static Cancellable claimAttemptCreate(Claim claim, Player creator) {
        PueblosEvent_ClaimAttemptCreate event = new PueblosEvent_ClaimAttemptCreate(claim, creator);
        callEvent(creator, event);
        return event;
    }

    public static void claimCreate(@Nullable CommandSender executor, Claim claim, Player creator) {
        PueblosEvent_ClaimCreate event = new PueblosEvent_ClaimCreate(claim, creator);
        callEvent(executor, event);
    }

    public static void claimDelete(CommandSender executor, ClaimMain claim) {
        PueblosEvent_ClaimDelete event = new PueblosEvent_ClaimDelete(claim);
        callEvent(executor, event);
    }

    public static Cancellable claimResize(CommandSender executor, Claim claim, Player editor, Vector loc_1, Vector loc_2) {
        PueblosEvent_ClaimResize event = new PueblosEvent_ClaimResize(claim, editor, loc_1, loc_2);
        callEvent(executor, event);
        if (event.isCancelled() && event.sendCancelledMessage())
            MessagesCore.EVENT_DENIED.send(editor);
        return event;
    }

    public static Cancellable memberLeave(CommandSender executor, ClaimMember member) {
        Event event = new PueblosEvent_MemberLeave(member);
        callEvent(executor, event);
        return (Cancellable) event;
    }

    public static Cancellable teleportToClaim(CommandSender executor, ClaimMain claim, Player player, Location from) {
        Event event = new PueblosEvent_ClaimTeleportTo(claim, player, from);
        callEvent(executor, event);
        return (Cancellable) event;
    }

    public static void command(CommandSender executor, PueblosCommand cmd) {
        Event event = new PueblosEvent_CommandExecuted(executor, cmd);
        callEvent(executor, event);
    }

    public static void claimWalked(Player p, ClaimMain claim, boolean walked_in) {
        Event event = walked_in ? new PueblosEvent_ClaimWalkedIn(claim, p) : new PueblosEvent_ClaimWalkedOut(claim, p);
        callEvent(p, event);
    }

    private static void callEvent(CommandSender executor, Event e) {
        Bukkit.getPluginManager().callEvent(e);
        //Send event denied message if enabled
        if (executor != null && e instanceof PueblosEventType_ClaimCancellable) {
            PueblosEventType_ClaimCancellable ev = (PueblosEventType_ClaimCancellable) e;
            if (ev.isCancelled() && ev.sendCancelledMessage())
                MessagesCore.EVENT_DENIED.send(executor);
        }
    }
}
