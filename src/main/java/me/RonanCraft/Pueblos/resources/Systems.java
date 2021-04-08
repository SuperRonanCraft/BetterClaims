package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.player.PlayerInfo;
import me.RonanCraft.Pueblos.player.events.EventListener;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.database.Database;
import me.RonanCraft.Pueblos.resources.database.SQLite;

public class Systems {
    private final Database database = new SQLite();
    private final ClaimHandler claim = new ClaimHandler();
    private final EventListener events = new EventListener();
    private final PlayerInfo playerInfo = new PlayerInfo();

    public void load() {
        database.load();
        claim.load();
        playerInfo.clear();
    }

    public Database getDatabase() {
        return database;
    }

    public ClaimHandler getClaimHandler() {
        return claim;
    }

    public EventListener getEvents() {
        return events;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

}
