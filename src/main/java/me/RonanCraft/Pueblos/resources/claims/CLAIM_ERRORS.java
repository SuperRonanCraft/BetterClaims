package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.claims.events.ClaimEvent_Create;
import org.bukkit.Bukkit;

public enum CLAIM_ERRORS {
    NONE,
    OVERLAPPING,
    SIZE,
    DATABASE_ERROR,
    LOCATION_ALREADY_EXISTS
}
