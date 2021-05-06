package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.events.PlayerClaimInteraction;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.claims.enums.*;
import me.RonanCraft.Pueblos.resources.claims.selling.ClaimAuctionManager;
import me.RonanCraft.Pueblos.resources.database.DatabaseClaims;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.RonanCraft.Pueblos.resources.tools.HelperEvent;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClaimHandler {
    private final ClaimAuctionManager auctionManager = new ClaimAuctionManager(this);
    private final List<ClaimMain> mainClaims = new ArrayList<>();
    private final List<ClaimChild> childClaims = new ArrayList<>();
    private int claim_maxSize = 256;

    private DatabaseClaims getDatabase() {
        return Pueblos.getInstance().getDatabaseClaims();
    }

    public void load() {
        mainClaims.clear();
        childClaims.clear();
        this.mainClaims.addAll(getDatabase().getClaims());
        //this.childClaims.addAll(getDatabase().getClaimsChild());
        claim_maxSize = Pueblos.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_MAXSIZE);
        if (claim_maxSize < 10)
            claim_maxSize = 10;
        auctionManager.load();
    }

    public ClaimMain loadClaim(ResultSet result) throws SQLException {
        UUID id = null;
        try {
            id = UUID.fromString(result.getString(DatabaseClaims.COLUMNS.OWNER_UUID.name));
        } catch (IllegalArgumentException e) {
            //id = UUID.randomUUID();
        }
        String name = result.getString(DatabaseClaims.COLUMNS.OWNER_NAME.name);
        try {
            ClaimMain claim;
            BoundingBox position = JSONEncoding.getPosition(result.getString(DatabaseClaims.COLUMNS.POSITION.name));
            if (result.getBoolean(DatabaseClaims.COLUMNS.ADMIN_CLAIM.name) || id == null)
                claim = new ClaimMain(position);
            else
                claim = new ClaimMain(id, name, position);
            //Members Load
            List<ClaimMember> members = JSONEncoding.getMember(result.getString(DatabaseClaims.COLUMNS.MEMBERS.name), claim);
            if (members != null)
                for (ClaimMember member : members)
                    claim.addMember(member, false);

            //Flags Load
            HashMap<CLAIM_FLAG, Object> flags = JSONEncoding.getFlags(result.getString(DatabaseClaims.COLUMNS.FLAGS.name));
            if (flags != null)
                for (Map.Entry<CLAIM_FLAG, Object> flag : flags.entrySet())
                    claim.getFlags().setFlag(flag.getKey(), flag.getValue(), false);

            //Join Requests Load
            List<ClaimRequest> requests = JSONEncoding.getRequests(result.getString(DatabaseClaims.COLUMNS.REQUESTS.name), claim);
            if (requests != null)
                for (ClaimRequest request : requests)
                    claim.addRequest(request, false);
            claim.claimId = result.getInt(DatabaseClaims.COLUMNS.CLAIM_ID.name);
            claim.dateCreated = HelperDate.getDate(result.getString(DatabaseClaims.COLUMNS.DATE.name));
            return claim;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        CLAIM_ERRORS error = isLocationValid(claim, creator, claimInteraction, ignoredClaim);
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

    private CLAIM_ERRORS isLocationValid(Claim claim, @Nullable Player p, @Nullable PlayerClaimInteraction claimInteraction, Claim ingnoredClaim) {
        Location greater = claim.getBoundingBox().getGreaterBoundaryCorner();
        Location lower = claim.getBoundingBox().getLesserBoundaryCorner();
        return isLocationValid(greater, lower, p, ingnoredClaim, claimInteraction);
    }

    public CLAIM_ERRORS isLocationValid(Location greater, Location lower, @Nullable Player p, @Nullable Claim claimIgnored, @Nullable PlayerClaimInteraction claimInteraction) {
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
            if (_claim == claimIgnored) //Ignore this claim
                continue;
            if (claimInteraction != null && _claim == claimInteraction.editing) //Ignore this claim
                continue;
            if (_claim.getBoundingBox().intersects(new BoundingBox(null, greater, lower))) { //Intersecting with this claim
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

    public List<ClaimMain> getClaims(@Nonnull UUID uuid) {
        List<ClaimMain> claims = new ArrayList<>();
        for (ClaimMain claim : this.mainClaims)
            if (claim.getOwnerID() != null && claim.getOwnerID().equals(uuid))
                claims.add(claim);
        return claims;
    }

    public ClaimMain getClaim(Location loc) {
        for (ClaimMain claim : this.mainClaims)
            if (claim.contains(loc))
                return claim;
        return null;
    }

    public ClaimMain getClaim(int claimId) {
        for (ClaimMain claim : this.mainClaims)
            if (claim.claimId == claimId)
                return claim;
        return null;
    }

    public List<ClaimMain> getMainClaims() {
        return mainClaims;
    }

    public boolean allowBreak(@Nonnull Player p, @Nonnull Location block_location) {
        ClaimMain claim = getClaim(block_location);
        if (claim == null)
            return true;
        else if (claim.isAdminClaim() && PermissionNodes.ADMIN_CLAIM.check(p)) //Is an admin CLAIM, and player is an admin
            return true;
        else if (Pueblos.getInstance().getPlayerData(p).isOverriding()) //Is an admin and wants to override claims
            return true;
        else
            return claim.canBuild(p);
    }

    public boolean allowInteract(Player p, Block block) {
        ClaimMain claim = getClaim(block.getLocation());
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

    public ClaimAuctionManager getAuctionManager() {
        return auctionManager;
    }

}
