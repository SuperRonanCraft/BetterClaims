/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ClaimMain extends Claim {

    public ClaimMain(UUID ownerId, String ownerName, BoundingBox boundingBox) {
        super(ownerId, ownerName, boundingBox);
    }

    public ClaimMain(BoundingBox position) {
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
                members.addMember(new ClaimMember(oldOwnerId, oldOwner.getName(), Calendar.getInstance().getTime(), this), true);
            }
        }
        if (this.ownerId != null && oldOwnerId != null) { //Remove the new owner as a member, if they were a member
            if (members.isMember(oldOwnerId))
                members.remove(members.getMember(oldOwnerId), true);
        }
        updated();
    }
}
