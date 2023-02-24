/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.resources.helper;

import me.RonanCraft.BetterClaims.customevents.*;
import me.RonanCraft.BetterClaims.player.command.PueblosCommand;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.Claim_Child;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HelperEvent {

    public static Cancellable claimAttemptCreate(ClaimData claimData, Player creator) {
        PueblosEvent_ClaimAttemptCreate event = new PueblosEvent_ClaimAttemptCreate(claimData, creator);
        callEvent(creator, event);
        return event;
    }

    public static void claimCreate(@Nullable CommandSender executor, ClaimData claimData, Player creator) {
        PueblosEvent_ClaimCreate event = new PueblosEvent_ClaimCreate(claimData, creator);
        callEvent(executor, event);
    }

    public static void claimDelete(CommandSender executor, Claim claim, List<Claim_Child> children) {
        PueblosEvent_ClaimDelete event = new PueblosEvent_ClaimDelete(claim, children);
        callEvent(executor, event);
    }

    public static Cancellable claimResize(CommandSender executor, ClaimData claimData, Player editor, Vector loc_1, Vector loc_2) {
        PueblosEvent_ClaimResize event = new PueblosEvent_ClaimResize(claimData, editor, loc_1, loc_2);
        callEvent(executor, event);
        if (event.isCancelled() && event.sendCancelledMessage())
            MessagesCore.EVENT_DENIED.send(editor);
        return event;
    }

    public static Cancellable memberLeave(CommandSender executor, Member member) {
        Event event = new PueblosEvent_MemberLeave(member);
        callEvent(executor, event);
        return (Cancellable) event;
    }

    public static Cancellable teleportToClaim(CommandSender executor, ClaimData claimData, Player player, Location from) {
        Event event = new PueblosEvent_ClaimTeleportTo(claimData, player, from);
        callEvent(executor, event);
        return (Cancellable) event;
    }

    public static void command(CommandSender executor, PueblosCommand cmd) {
        Event event = new PueblosEvent_CommandExecuted(executor, cmd);
        callEvent(executor, event);
    }

    public static void claimWalked(Player p, Claim claim, boolean walked_in) {
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
