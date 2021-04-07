package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.database.Database;
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

    public void load() {
        claims.clear();
        List<Claim> claims = Pueblos.getInstance().getSystems().getDatabase().getClaims();
        this.claims.addAll(claims);
    }

    public CLAIM_ERRORS addClaim(Claim claim, Player p, boolean save) {
        CLAIM_ERRORS error = isClaimValid(claim, p);
        if (error == CLAIM_ERRORS.NONE) {
            claims.add(claim);
            if (save)
                if (Pueblos.getInstance().getSystems().getDatabase().createClaim(claim))
                    return CLAIM_ERRORS.NONE;
                else
                    return CLAIM_ERRORS.DATABASE_ERROR;
        }
        return error;
    }

    private CLAIM_ERRORS isClaimValid(Claim claim, Player p) {
        Location greater = claim.getGreaterBoundaryCorner();
        Location lower = claim.getLesserBoundaryCorner();
        //Size
        if (Math.abs(greater.getBlockX() - lower.getBlockX()) < 10)
            return CLAIM_ERRORS.SIZE;
        else if (Math.abs(greater.getBlockZ() - lower.getBlockZ()) < 10)
            return CLAIM_ERRORS.SIZE;
        //Overlapping
        int x1 = lower.getBlockX();
        int x2 = greater.getBlockX();
        int y1 = lower.getBlockZ();
        int y2 = greater.getBlockZ();
        for (Claim _claim : claims) {
            Location greater_2 = _claim.getGreaterBoundaryCorner();
            Location lower_2 = _claim.getLesserBoundaryCorner();
            int x3 = lower_2.getBlockX();
            int x4 = greater_2.getBlockX();
            int y3 = lower_2.getBlockZ();
            int y4 = greater_2.getBlockZ();
            if (!(x3 > x2 || y3 > y2 || x1 > x4 || y1 > y4)) {
                Visualization.fromClaim(claim, p.getLocation().getBlockY(), VisualizationType.ErrorClaim, p.getLocation()).apply(p);
                return CLAIM_ERRORS.OVERLAPPING;
            }
        }
        return CLAIM_ERRORS.NONE;
    }

    public List<Claim> getClaims(UUID uuid) {
        List<Claim> claims = new ArrayList<>();
        for (Claim claim : this.claims) {
            if (claim.ownerId.equals(uuid))
                claims.add(claim);
        }
        return claims;
    }

    public Claim getClaim(Location loc) {
        for (Claim claim : this.claims) {
            if (claim.contains(loc))
                return claim;
        }
        return null;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public Claim loadClaim(ResultSet result) throws SQLException {
        UUID id;
        try {
            id = UUID.fromString(result.getString(Database.COLUMNS.OWNER_UUID.name));
        } catch (IllegalArgumentException e) {
            id = UUID.randomUUID();
        }
        String name = result.getString(Database.COLUMNS.OWNER_NAME.name);
        try {
            return new Claim(id, name, JSONEncoding.getPosition(result.getString(Database.COLUMNS.POSITION.name)));
        } catch (Exception e) {
            return null;
        }
    }

    public Claim claimCreate(UUID owner, String name, ClaimPosition position) {
        return new Claim(owner, name, position);
    }
}
