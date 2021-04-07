package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.database.Database;
import me.RonanCraft.Pueblos.resources.tools.JSONEncoding;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClaimHandler {
    private final HashMap<UUID, Claim> claims = new HashMap<>();

    public void load() {
        claims.clear();
        List<Claim> claims = Pueblos.getInstance().getSystems().getDatabase().getClaims();
        for (Claim claim : claims) {
            this.claims.put(claim.ownerId, claim);
        }
    }

    public void addClaim(Claim claim, boolean save) {
        if (!claims.containsKey(claim.ownerId)) {
            claims.put(claim.ownerId, claim);
            if (save)
                Pueblos.getInstance().getSystems().getDatabase().createClaim(claim);
        }
    }

    public Claim getClaim(UUID uuid) {
        return claims.getOrDefault(uuid, null);
    }

    public Set<Map.Entry<UUID, Claim>> getClaims() {
        return claims.entrySet();
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
