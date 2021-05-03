package me.RonanCraft.Pueblos.resources.claims;

import java.util.UUID;

public interface ClaimInfo {

    void updated();

    boolean wasUpdated();

    void uploaded();

    UUID getOwnerID();

    String getOwnerName();

    String getClaimName();
}
