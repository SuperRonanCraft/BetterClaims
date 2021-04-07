package me.RonanCraft.Pueblos.resources.claims;

import java.util.UUID;

public class ClaimMember {

    private final UUID id;
    private final String name;
    private final boolean owner;

    public ClaimMember(UUID id, String name, boolean owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }
}
