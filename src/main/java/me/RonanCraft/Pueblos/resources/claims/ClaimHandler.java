package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.events.PlayerClaimInteraction;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.claims.enums.*;
import me.RonanCraft.Pueblos.resources.claims.selling.AuctionManager;
import me.RonanCraft.Pueblos.resources.database.DatabaseClaims;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ClaimHandler {
    private final AuctionManager auctionManager = new AuctionManager(this);
    private final List<ClaimMain> mainClaims = new ArrayList<>();
    private final List<ClaimChild> childClaims = new ArrayList<>();
    private int claim_maxSize = 256;

    private DatabaseClaims getDatabase() {
        return Pueblos.getInstance().getDatabaseClaims();
    }

    public void load() {
        mainClaims.clear();
        childClaims.clear();
        HashMap<CLAIM_TYPE, List<Claim>> databaseClaims = getDatabase().getClaims();
        if (databaseClaims.get(CLAIM_TYPE.MAIN) != null)
            for (Claim claim : databaseClaims.get(CLAIM_TYPE.MAIN))
                this.mainClaims.add((ClaimMain) claim);
        if (databaseClaims.get(CLAIM_TYPE.CHILD) != null)
            for (Claim claim : databaseClaims.get(CLAIM_TYPE.CHILD))
                this.childClaims.add((ClaimChild) claim);
        //this.childClaims.addAll(getDatabase().getClaimsChild());
        claim_maxSize = Pueblos.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_MAXSIZE);
        if (claim_maxSize < 10)
            claim_maxSize = 10;
        auctionManager.load();
    }

    public CLAIM_ERRORS changeOwner(ClaimMain claim, boolean save_oldOwner, @Nullable UUID id, boolean adminClaim) {
        claim.changeOwner(adminClaim ? null : id, save_oldOwner);
        if (getDatabase().saveClaim(claim))
            return CLAIM_ERRORS.NONE;
        else //Something bad happened, reload all claims just in-case something broke
            load();
        return CLAIM_ERRORS.DATABASE_ERROR;
    }

    public CLAIM_ERRORS uploadCreatedClaim(Claim claim, @Nullable Player creator, @Nullable PlayerClaimInteraction claimInteraction) {
        Claim ignoredClaim = claim instanceof ClaimChild ? ((ClaimChild) claim).getParent() : null;
        CLAIM_ERRORS error = isLocationValid(claim, creator, claimInteraction, new ArrayList<>(Collections.singletonList(ignoredClaim)));
        if (error == CLAIM_ERRORS.NONE) {
            claim.dateCreated = Calendar.getInstance().getTime();
            if (getDatabase().createClaim(claim)) {
                //Add claim to cache
                if (claim instanceof ClaimMain)
                    mainClaims.add((ClaimMain) claim);
                else if (claim instanceof ClaimChild)
                    childClaims.add((ClaimChild) claim);
                if (creator != null)
                    HelperEvent.claimCreate(creator, claim, null);
                return CLAIM_ERRORS.NONE;
            } else
                return CLAIM_ERRORS.DATABASE_ERROR;
        }
        return error;
    }

    public CLAIM_ERRORS deleteClaim(Player deletor, ClaimMain claim) {
        CLAIM_ERRORS error = CLAIM_ERRORS.NONE;
        List<ClaimChild> childrenClaims = new ArrayList<>();
        for (ClaimChild child : this.childClaims)
            if (child.getParent() == claim)
                childrenClaims.add(child);
        //DELETE ALL THESE CHILDREN TOO!
        if (getDatabase().deleteClaim(claim)) {
            mainClaims.remove(claim);
            HelperEvent.claimDelete(deletor, claim);
        } else
            error = CLAIM_ERRORS.DATABASE_ERROR;
        return error;
    }

    private CLAIM_ERRORS isLocationValid(Claim claim, @Nullable Player p, @Nullable PlayerClaimInteraction claimInteraction, List<Claim> ingnoredClaim) {
        Location greater = claim.getGreaterBoundaryCorner();
        Location lower = claim.getLesserBoundaryCorner();
        return isLocationValid(greater, lower, p, ingnoredClaim, claimInteraction);
    }

    public CLAIM_ERRORS isLocationValid(Location greater, Location lower, @Nullable Player p, @Nullable List<Claim> claimIgnored, @Nullable PlayerClaimInteraction claimInteraction) {
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
        for (ClaimMain _claim : mainClaims) {
            if (claimIgnored != null && claimIgnored.contains(_claim)) {
                if (claimInteraction != null)
                    for (ClaimChild child : Pueblos.getInstance().getClaimHandler().getClaimsChild(_claim)) { //Dont allow overlapping children when resizing child
                        if (!claimIgnored.contains(child)) //Ignore the child being resized
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

    public List<Claim> getClaims(@Nonnull UUID uuid) {
        List<Claim> claims = new ArrayList<>();
        for (Claim claim : this.mainClaims)
            if (claim.getOwnerID() != null && claim.getOwnerID().equals(uuid))
                claims.add(claim);
        return claims;
    }

    public ClaimMain getClaimMain(Location loc) {
        for (ClaimMain claim : this.mainClaims)
            if (claim.contains(loc))
                return claim;
        return null;
    }

    public ClaimMain getClaimMain(int id) {
        for (ClaimMain claim : this.mainClaims)
            if (claim.claimId == id)
                return claim;
        return null;
    }

    public ClaimChild getClaimChild(Location loc) {
        for (ClaimChild claim : this.childClaims)
            if (claim.contains(loc))
                return claim;
        return null;
    }

    @Nullable
    public Claim getClaim(int claimId) {
        for (Claim claim : getClaimsAll())
            if (claim.claimId == claimId)
                return claim;
        return null;
    }

    public Claim getClaimAt(Location loc, boolean ignoreChild) {
        for (ClaimMain claim : this.mainClaims)
            if (claim.contains(loc)) {
                if (!ignoreChild) {
                    List<ClaimChild> children = getClaimsChild(claim);
                    for (ClaimChild child : children)
                        if (child.contains(loc))
                            return child; //Return the child claim first
                }
                return claim; //Return the parent if no children in this location
            }
        return null;
    }

    public List<ClaimMain> getClaimsMain() {
        return mainClaims;
    }

    public List<ClaimChild> getClaimsChild() {
        return childClaims;
    }

    public List<ClaimChild> getClaimsChild(ClaimMain claimMain) {
        List<ClaimChild> claimChildren = new ArrayList<>();
        for (ClaimChild child : getClaimsChild())
            if (child.getParent().equals(claimMain))
                claimChildren.add(child);
        return claimChildren;
    }

    public List<Claim> getClaimsAll() {
        List<Claim> claims = new ArrayList<>(getClaimsMain());
        claims.addAll(getClaimsChild());
        return claims;
    }

    public boolean allowBreak(@Nonnull Player p, @Nonnull Location block_location) {
        Claim claim = getClaimAt(block_location, false); //Grab the parent or the child claim if one here
        if (claim == null)
            return true;
        else if (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p)) //Is an admin CLAIM, and player is an admin
            return true;
        else if (Pueblos.getInstance().getPlayerData(p).isOverriding()) //Is an admin and wants to override claims
            return true;
        else {
            return claim.canBuild(p);
        }
    }

    public boolean allowInteract(Player p, Block block) {
        Claim claim = getClaimAt(block.getLocation(), false);
        if (claim == null || (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p))) { //No claim here or is an admin claim
            return true;
        } else if (claim.isAdminClaim()) {
            return false;
        } else if (claim.isOwner(p)) {
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
        ClaimMember member = claim.getMember(p);
        if (member != null) { //Is this player a member of this claim?
            if (flag != null) {
                //Check member flag value (if it exists)
                CLAIM_FLAG_MEMBER memberFlag = flag.getMemberEquivalent();
                Object flagValue = claim.getFlags().getFlag(flag); //Get the claims flag value
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
            Object flagValue = claim.getFlags().getFlag(flag); //Get the claims flag value
            return ((Boolean) flagValue);
        }
    }

    public HashMap<CLAIM_FLAG, Object> getFlagsAt(Location loc, boolean asMember) {
        Claim claim = getClaimAt(loc, false);
        return null;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public boolean canResize(Player editor, Claim claim, BoundingBox newBox) {
        if (claim instanceof ClaimMain) {
            List<ClaimChild> claimChildren = getClaimsChild((ClaimMain) claim);
            for (ClaimChild child : claimChildren)
                if (!newBox.contains(child.getBoundingBox())) { //Are all children contained in this new box?
                    Visualization.fromClaim(child, editor.getLocation().getBlockY(), VisualizationType.ERROR, editor.getLocation());
                    return false;
                }
            return true;
        } else {
            ClaimChild claimChild = (ClaimChild) claim;
            return claimChild.getParent().getBoundingBox().contains(newBox);
        }
    }
}
