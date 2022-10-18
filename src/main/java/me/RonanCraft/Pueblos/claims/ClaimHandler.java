/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.claims.data.BoundingBox;
import me.RonanCraft.Pueblos.player.events.PlayerClaimInteraction;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_FLAG;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.claims.enums.CLAIM_TYPE;
import me.RonanCraft.Pueblos.claims.data.members.Member;
import me.RonanCraft.Pueblos.auction.AuctionManager;
import me.RonanCraft.Pueblos.database.DatabaseClaims;
import me.RonanCraft.Pueblos.resources.helper.HelperEvent;
import me.RonanCraft.Pueblos.resources.visualization.Visualization;
import me.RonanCraft.Pueblos.resources.visualization.VisualizationType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClaimHandler {
    private final AuctionManager auctionManager = new AuctionManager(this);
    private final List<Claim> mainClaims = new ArrayList<>();
    private final List<Claim_Child> childClaims = new ArrayList<>();
    private int claim_maxSize = 256;

    private DatabaseClaims getDatabase() {
        return Pueblos.getInstance().getDatabaseClaims();
    }

    public void load() {
        mainClaims.clear();
        childClaims.clear();
        HashMap<CLAIM_TYPE, List<ClaimData>> databaseClaims = getDatabase().getClaims();
        if (databaseClaims.get(CLAIM_TYPE.MAIN) != null)
            for (ClaimData claimData : databaseClaims.get(CLAIM_TYPE.MAIN))
                this.mainClaims.add((Claim) claimData);
        if (databaseClaims.get(CLAIM_TYPE.CHILD) != null)
            for (ClaimData claimData : databaseClaims.get(CLAIM_TYPE.CHILD))
                this.childClaims.add((Claim_Child) claimData);
        //this.childClaims.addAll(getDatabase().getClaimsChild());
        claim_maxSize = Pueblos.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_MAXSIZE);
        if (claim_maxSize < 10)
            claim_maxSize = 10;
        auctionManager.load();
    }

    public CLAIM_ERRORS changeOwner(Claim claim, boolean save_oldOwner, @Nullable UUID id, boolean adminClaim) {
        claim.changeOwner(adminClaim ? null : id, save_oldOwner);
        if (getDatabase().saveClaim(claim))
            return CLAIM_ERRORS.NONE;
        else //Something bad happened, reload all claims just in-case something broke
            load();
        return CLAIM_ERRORS.DATABASE_ERROR;
    }

    public CLAIM_ERRORS uploadCreatedClaim(ClaimData claimData, @Nullable Player creator, @Nullable PlayerClaimInteraction claimInteraction) {
        ClaimData ignoredClaimData = claimData instanceof Claim_Child ? ((Claim_Child) claimData).getParent() : null;
        CLAIM_ERRORS error = isLocationValid(claimData, creator, claimInteraction, new ArrayList<>(Collections.singletonList(ignoredClaimData)));
        if (error == CLAIM_ERRORS.NONE) {
            claimData.dateCreated = Calendar.getInstance().getTime();
            if (getDatabase().createClaim(claimData)) {
                //Add claim to cache
                if (claimData instanceof Claim)
                    mainClaims.add((Claim) claimData);
                else if (claimData instanceof Claim_Child)
                    childClaims.add((Claim_Child) claimData);
                if (creator != null)
                    HelperEvent.claimCreate(creator, claimData, null);
                return CLAIM_ERRORS.NONE;
            } else
                return CLAIM_ERRORS.DATABASE_ERROR;
        }
        return error;
    }

    public CLAIM_ERRORS deleteClaim(Player deletor, Claim claim) {
        CLAIM_ERRORS error = CLAIM_ERRORS.NONE;
        List<Claim_Child> childrenClaims = getClaimsChild(claim);
        //DELETE ALL THESE CHILDREN TOO!
        if (getDatabase().deleteClaim(claim, childrenClaims)) {
            this.mainClaims.remove(claim);
            this.childClaims.removeAll(childrenClaims);
            HelperEvent.claimDelete(deletor, claim, childrenClaims);
        } else
            error = CLAIM_ERRORS.DATABASE_ERROR;
        return error;
    }

    private CLAIM_ERRORS isLocationValid(ClaimData claimData, @Nullable Player p, @Nullable PlayerClaimInteraction claimInteraction, List<ClaimData> ingnoredClaimData) {
        Location greater = claimData.getGreaterBoundaryCorner();
        Location lower = claimData.getLesserBoundaryCorner();
        return isLocationValid(greater, lower, p, ingnoredClaimData, claimInteraction);
    }

    public CLAIM_ERRORS isLocationValid(Location greater, Location lower, @Nullable Player p, @Nullable List<ClaimData> claimDataIgnored, @Nullable PlayerClaimInteraction claimInteraction) {
        //Size
        if (Math.abs(greater.getBlockX() - lower.getBlockX()) < 10 || Math.abs(greater.getBlockZ() - lower.getBlockZ()) < 10) {
            if (p != null)
                Visualization.fromLocation(lower, greater, p.getLocation().getBlockY(), VisualizationType.ERROR_SMALL, p.getLocation()).apply(p);
            return CLAIM_ERRORS.SIZE_SMALL;
        } else if (Math.abs(greater.getBlockX() - lower.getBlockX()) > claim_maxSize || Math.abs(greater.getBlockZ() - lower.getBlockZ()) > claim_maxSize) {
            if (p != null)
                Visualization.fromLocation(lower, greater, p.getLocation().getBlockY(), VisualizationType.ERROR_LARGE, p.getLocation()).apply(p);
            return CLAIM_ERRORS.SIZE_LARGE;
        }
        //Overlapping
        //int x1 = lower.getBlockX();
        //int x2 = greater.getBlockX();
        //int y1 = lower.getBlockZ();
        //int y2 = greater.getBlockZ();
        for (Claim _claim : mainClaims) {
            if (claimDataIgnored != null && claimDataIgnored.contains(_claim)) {
                if (claimInteraction != null && claimInteraction.editing instanceof Claim_Child)
                    for (Claim_Child child : Pueblos.getInstance().getClaimHandler().getClaimsChild(_claim)) { //Dont allow overlapping children when resizing child
                        if (!claimDataIgnored.contains(child)) //Ignore the child being resized
                            if (child.getBoundingBox().intersects(new BoundingBox(greater, lower))) {
                                if (p != null)
                                    Visualization.fromClaim(_claim, p.getLocation().getBlockY(), VisualizationType.ERROR, p.getLocation()).apply(p);
                                return CLAIM_ERRORS.OVERLAPPING;
                            }
                    }
                continue;
            }
            if (claimInteraction != null && _claim == claimInteraction.editing) //Ignore this claim
                continue;
            if (_claim.getBoundingBox().intersects(new BoundingBox(greater, lower))) { //Intersecting with this claim
                if (p != null)
                    Visualization.fromClaim(_claim, p.getLocation().getBlockY(), VisualizationType.ERROR, p.getLocation()).apply(p);
                return CLAIM_ERRORS.OVERLAPPING;
            }
            /*Location greater_2 = _claim.getBoundingBox().getGreaterBoundaryCorner();
            Location lower_2 = _claim.getBoundingBox().getLesserBoundaryCorner();
            int x3 = lower_2.getBlockX();
            int x4 = greater_2.getBlockX();
            int y3 = lower_2.getBlockZ();
            int y4 = greater_2.getBlockZ();
            if (!(x3 > x2 || y3 > y2 || x1 > x4 || y1 > y4)) {
                if (p != null)
                    Visualization.fromClaim(_claim, p.getLocation().getBlockY(), VisualizationType.ERROR, p.getLocation()).apply(p);
                return CLAIM_ERRORS.OVERLAPPING;
            }*/
        }
        return CLAIM_ERRORS.NONE;
    }

    public List<ClaimData> getClaims(@NotNull UUID uuid) {
        List<ClaimData> claimData = new ArrayList<>();
        for (ClaimData claim : this.mainClaims)
            if (claim.getOwnerID() != null && claim.getOwnerID().equals(uuid))
                claimData.add(claim);
        return claimData;
    }

    public Claim getClaimMain(Location loc) {
        for (Claim claim : this.mainClaims)
            if (claim.contains(loc))
                return claim;
        return null;
    }

    public Claim getClaimMain(int id) {
        for (Claim claim : this.mainClaims)
            if (claim.claimId == id)
                return claim;
        return null;
    }

    public Claim_Child getClaimChild(Location loc) {
        for (Claim_Child claim : this.childClaims)
            if (claim.contains(loc))
                return claim;
        return null;
    }

    @Nullable
    public ClaimData getClaim(int claimId) {
        for (ClaimData claimData : getClaimsAll())
            if (claimData.claimId == claimId)
                return claimData;
        return null;
    }

    public ClaimData getClaimAt(Location loc, boolean ignoreChild) {
        for (Claim claim : this.mainClaims)
            if (claim.contains(loc)) {
                if (!ignoreChild) {
                    List<Claim_Child> children = getClaimsChild(claim);
                    for (Claim_Child child : children)
                        if (child.contains(loc))
                            return child; //Return the child claim first
                }
                return claim; //Return the parent if no children in this location
            }
        return null;
    }

    public List<Claim> getClaimsMain() {
        return mainClaims;
    }

    public List<Claim_Child> getClaimsChild() {
        return childClaims;
    }

    public List<Claim_Child> getClaimsChild(Claim claim) {
        List<Claim_Child> claimChildren = new ArrayList<>();
        for (Claim_Child child : getClaimsChild())
            if (child.getParent().equals(claim))
                claimChildren.add(child);
        return claimChildren;
    }

    public List<ClaimData> getClaimsAll() {
        List<ClaimData> claimData = new ArrayList<>(getClaimsMain());
        claimData.addAll(getClaimsChild());
        return claimData;
    }

    public boolean allowBreak(@NotNull Player p, @NotNull Location block_location) {
        ClaimData claimData = getClaimAt(block_location, false); //Grab the parent or the child claim if one here
        if (claimData == null)
            return true;
        else if (claimData.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p)) //Is an admin CLAIM, and player is an admin
            return true;
        else if (Pueblos.getInstance().getPlayerData(p).isOverriding()) //Is an admin and wants to override claims
            return true;
        else {
            return claimData.canBuild(p);
        }
    }

    public boolean allowInteract(Player p, Block block) {
        ClaimData claimData = getClaimAt(block.getLocation(), false);
        if (claimData == null || (claimData.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p))) { //No claim here or is an admin claim
            return true;
        } else if (claimData.isAdminClaim()) {
            return false;
        } else if (claimData.isOwner(p)) {
            return true;
        }
        //There is a claim, and not the owner
        CLAIM_FLAG flag = null;
        if (block.getType().name().contains("LEVER")) {
            flag = CLAIM_FLAG.ALLOW_LEVER;
        } else if (block.getType().name().contains("DOOR")) {
            flag = CLAIM_FLAG.ALLOW_DOOR;
        } else if (block.getType().name().contains("BUTTON")) {
            flag = CLAIM_FLAG.ALLOW_BUTTON;
        } else if (block.getType().name().contains("BED")) {
            flag = CLAIM_FLAG.ALLOW_BED;
        }
        Member member = claimData.getMember(p);
        if (member != null) { //Is this player a member of this claim?
            if (flag != null) {
                //Check member flag value (if it exists)
                CLAIM_FLAG_MEMBER memberFlag = flag.getMemberEquivalent();
                Object flagValue = claimData.getFlags().getFlag(flag); //Get the claims flag value
                if (memberFlag != null)
                    flagValue = member.getFlags().getOrDefault(memberFlag, memberFlag.getDefault()); //Get the members flag value
                return ((Boolean) flagValue); //Are they allowed to do this here?
            } else { //Blocks with inventories specific to claim members
                CLAIM_FLAG_MEMBER memberFlag = null;
                if (block.getState() instanceof InventoryHolder)
                    memberFlag = CLAIM_FLAG_MEMBER.ALLOW_CHEST;
                Object flagValue = null;
                if (memberFlag != null)
                    flagValue = member.getFlags().getOrDefault(memberFlag, memberFlag.getDefault());
                if (flagValue != null)
                    return ((Boolean) flagValue); //Are they allowed to do this here?
            }
            return true;
        } else { //Cancel interactions if the claim flag is enabled
            Object flagValue = claimData.getFlags().getFlag(flag); //Get the claims flag value
            return ((Boolean) flagValue);
        }
    }

    public HashMap<CLAIM_FLAG, Object> getFlagsAt(Location loc, boolean asMember) {
        ClaimData claimData = getClaimAt(loc, false);
        return null;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public boolean canResize(Player editor, ClaimData claimData, BoundingBox newBox) {
        if (claimData instanceof Claim) {
            List<Claim_Child> claimChildren = getClaimsChild((Claim) claimData);
            for (Claim_Child child : claimChildren)
                if (!newBox.contains(child.getBoundingBox())) { //Are all children contained in this new box?
                    Visualization.fromClaim(child, editor.getLocation().getBlockY(), VisualizationType.ERROR, editor.getLocation());
                    return false;
                }
            return true;
        } else {
            Claim_Child claimChild = (Claim_Child) claimData;
            return claimChild.getParent().getBoundingBox().contains(newBox);
        }
    }
}
