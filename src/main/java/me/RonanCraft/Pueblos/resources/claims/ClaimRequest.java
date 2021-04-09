package me.RonanCraft.Pueblos.resources.claims;

import java.util.Date;
import java.util.UUID;

public class ClaimRequest {
    public final UUID id;
    public final String name;
    public final Date date;

    public ClaimRequest(UUID id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }
}
