package me.RonanCraft.Pueblos.resources.claims;

import java.util.*;

public class ClaimHandler {
    private final HashMap<UUID, Claim> claims = new HashMap<>();

    public void addClaim(Claim claim) {
        if (!claims.containsKey(claim.ownerId))
            claims.put(claim.ownerId, claim);
    }

    public Claim getClaim(UUID uuid) {
        return claims.getOrDefault(uuid, null);
    }

    public Set<Map.Entry<UUID, Claim>> getClaims() {
        return claims.entrySet();
    }
}
