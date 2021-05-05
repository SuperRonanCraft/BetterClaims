/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class Claim extends ClaimUpdates {

    public long claimId;
    UUID ownerId;
    private String ownerName;
    private final BoundingBox boundingBox;
    //Loaded after
    private String claimName;
    private final ClaimFlags flags = new ClaimFlags(this);
    private final ClaimMembers members = new ClaimMembers(this);
    private final List<ClaimRequest> requests = new ArrayList<>();
    //Claim info
    private boolean adminClaim;

    Claim(UUID ownerId, String ownerName, BoundingBox boundingBox) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.boundingBox = boundingBox;
        this.adminClaim = this.ownerId == null;
    }

    public UUID getOwnerID() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getClaimName() {
        return claimName != null ? claimName : (ownerName != null ? ownerName : (!isAdminClaim() ? getOwner().getName() : "Admin Claim"));
    }

    public abstract BoundingBox getBoundingBox();

    public abstract ClaimFlags getFlags();

    public boolean isAdminClaim() {
        return adminClaim;
    }

    @Nullable
    public abstract OfflinePlayer getOwner();

    public abstract void removeMember(ClaimMember member, boolean b);


    public String getBoundingBoxJSON() {
        return JSONEncoding.getJsonFromBoundingBox(getBoundingBox());
    }

    public abstract boolean checkPermLevel(Player p, CLAIM_PERMISSION_LEVEL claimLevel);

    public abstract List<ClaimMember> getMembers();

    public List<ClaimRequest> getRequests() {
        return requests;
    }

    public void removeRequest(ClaimRequest request, boolean update) {
        if (update)
            updated();
        requests.remove(request);
    }
}
