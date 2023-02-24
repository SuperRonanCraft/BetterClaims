/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Claim extends ClaimData {

    public Claim(UUID ownerId, String ownerName, @NotNull BoundingBox boundingBox) {
        super(ownerId, ownerName, boundingBox);
    }

    public Claim(BoundingBox position) {
        super(position);
    }

    void changeOwner(@Nullable UUID newOwnerId, boolean save_oldOwner_as_Member) {
        UUID oldOwnerId = this.ownerId;
        this.ownerId = newOwnerId;
        this.ownerName = getOwner() == null ? "Admin Claim" : getOwner().getName();
        this.adminClaim = this.ownerId == null;
        if (save_oldOwner_as_Member && oldOwnerId != null) { //Make old owner a member
            OfflinePlayer oldOwner = Bukkit.getPlayer(oldOwnerId);
            if (oldOwner != null) {
                members.addMember(new Member(oldOwnerId, oldOwner.getName(), Calendar.getInstance().getTime(), this), true);
            }
        }
        if (this.ownerId != null && oldOwnerId != null) { //Remove the new owner as a member, if they were a member
            if (members.isMember(oldOwnerId))
                members.remove(members.getMember(oldOwnerId), true);
        }
        updated(true);
    }

    public List<ClaimData> getChildren() {
        return new ArrayList<>(BetterClaims.getInstance().getClaimHandler().getClaimsChild(this));
    }
}
