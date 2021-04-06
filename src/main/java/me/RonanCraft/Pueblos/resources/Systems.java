package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.events.move.EventListener;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.database.Database;
import me.RonanCraft.Pueblos.resources.database.SQLite;

public class Systems {
    private final Database database = new SQLite();
    private final ClaimHandler claim = new ClaimHandler();
    private final EventListener events = new EventListener();

    public Database getDatabase() {
        return database;
    }

    public ClaimHandler getClaimHandler() {
        return claim;
    }

    public EventListener getEvents() {
        return events;
    }
}
