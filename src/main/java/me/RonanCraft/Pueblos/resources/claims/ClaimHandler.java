package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.database.DatabaseClaims;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import me.RonanCraft.Pueblos.resources.tools.visual.VisualizationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClaimHandler {
    private final List<Claim> claims = new ArrayList<>();
    private int claim_maxSize = 256;
    private final List<UUID> adminClaimMode = new ArrayList<>();

    public void load() {
        claims.clear();
        List<Claim> claims = Pueblos.getInstance().getSystems().getClaimDatabase().getClaims();
        this.claims.addAll(claims);
        claim_maxSize = Pueblos.getInstance().getSystems().getSettings().getInt(Settings.SETTING.CLAIM_MAXSIZE);
        if (claim_maxSize < 10)
            claim_maxSize = 10;
    }

    public CLAIM_ERRORS uploadClaim(Claim claim, Player p) {
        CLAIM_ERRORS error = isLocationValid(claim, p);
        if (error == CLAIM_ERRORS.NONE) {
            claim.dateCreated = Calendar.getInstance().getTime();
            if (Pueblos.getInstance().getSystems().getClaimDatabase().createClaim(claim)) {
                claims.add(claim);
                ClaimEvents.create(claim);
                return CLAIM_ERRORS.NONE;
            } else
                return CLAIM_ERRORS.DATABASE_ERROR;
        }
        return error;
    }

    private CLAIM_ERRORS isLocationValid(Claim claim, Player p) {
        Location greater = claim.getPosition().getGreaterBoundaryCorner();
        Location lower = claim.getPosition().getLesserBoundaryCorner();
        //Size
        return isLocationValid(greater, lower, p, null);
    }

    public CLAIM_ERRORS isLocationValid(Location greater, Location lower, Player p, Claim claimIgnored) {
        //Size
        if (Math.abs(greater.getBlockX() - lower.getBlockX()) < 10 || Math.abs(greater.getBlockZ() - lower.getBlockZ()) < 10) {
            Visualization.fromLocation(lower, greater, p.getLocation().getBlockY(), VisualizationType.ERROR_SMALL, p.getLocation()).apply(p);
            return CLAIM_ERRORS.SIZE_SMALL;
        } else if (Math.abs(greater.getBlockX() - lower.getBlockX()) > claim_maxSize || Math.abs(greater.getBlockZ() - lower.getBlockZ()) > claim_maxSize) {
            Visualization.fromLocation(lower, greater, p.getLocation().getBlockY(), VisualizationType.ERROR_LARGE, p.getLocation()).apply(p);
            return CLAIM_ERRORS.SIZE_LARGE;
        }
        //Overlapping
        int x1 = lower.getBlockX();
        int x2 = greater.getBlockX();
        int y1 = lower.getBlockZ();
        int y2 = greater.getBlockZ();
        for (Claim _claim : claims) {
            if (claimIgnored != null && _claim == claimIgnored) //Ignore this claim
                continue;
            Location greater_2 = _claim.getPosition().getGreaterBoundaryCorner();
            Location lower_2 = _claim.getPosition().getLesserBoundaryCorner();
            int x3 = lower_2.getBlockX();
            int x4 = greater_2.getBlockX();
            int y3 = lower_2.getBlockZ();
            int y4 = greater_2.getBlockZ();
            if (!(x3 > x2 || y3 > y2 || x1 > x4 || y1 > y4)) {
                Visualization.fromClaim(_claim, p.getLocation().getBlockY(), VisualizationType.ERROR, p.getLocation()).apply(p);
                return CLAIM_ERRORS.OVERLAPPING;
            }
        }
        return CLAIM_ERRORS.NONE;
    }

    public List<Claim> getClaims(UUID uuid) {
        List<Claim> claims = new ArrayList<>();
        for (Claim claim : this.claims)
            if (claim.ownerId.equals(uuid))
                claims.add(claim);
        return claims;
    }

    public Claim getClaim(Location loc) {
        for (Claim claim : this.claims)
            if (claim.contains(loc))
                return claim;
        return null;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public Claim loadClaim(ResultSet result) throws SQLException {
        UUID id;
        try {
            id = UUID.fromString(result.getString(DatabaseClaims.COLUMNS.OWNER_UUID.name));
        } catch (IllegalArgumentException e) {
            id = UUID.randomUUID();
        }
        String name = result.getString(DatabaseClaims.COLUMNS.OWNER_NAME.name);
        try {
            Claim claim = new Claim(id, name, JSONEncoding.getPosition(result.getString(DatabaseClaims.COLUMNS.POSITION.name)));
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

    public Claim claimCreate(UUID owner, String name, ClaimPosition position) {
        if (adminClaimMode.contains(owner))
            return new Claim(position);
        else
            return new Claim(owner, name, position);
    }

    public void toggleAdminClaimMode(UUID playerId) {
        if (!adminClaimMode.contains(playerId))
            adminClaimMode.add(playerId);
        else
            adminClaimMode.remove(playerId);
    }

    public void removeAdminClaimMode(UUID playerId) {
        adminClaimMode.remove(playerId);
    }
}
