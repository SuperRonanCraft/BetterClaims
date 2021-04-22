package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.inventory.GlobalItems;
import me.RonanCraft.Pueblos.player.PlayerInfo;
import me.RonanCraft.Pueblos.player.events.EventListener;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.database.DatabaseClaims;
import me.RonanCraft.Pueblos.resources.database.SQLite;

public class Systems {

    private final Settings settings = new Settings();
    private final DatabaseClaims claimDatabase = new DatabaseClaims();
    private final ClaimHandler claim = new ClaimHandler();
    private final EventListener events = new EventListener();
    private final PlayerInfo playerInfo = new PlayerInfo();
    private final GlobalItems globalItems = new GlobalItems();

    public void load() {
        settings.load(); //Load first
        claimDatabase.load();
        claim.load();
        playerInfo.clear();
        globalItems.load();
    }

    public DatabaseClaims getClaimDatabase() {
        return claimDatabase;
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

    public GlobalItems getGlobalItems() {
        return globalItems;
    }

    public Settings getSettings() {
        return settings;
    }
}
