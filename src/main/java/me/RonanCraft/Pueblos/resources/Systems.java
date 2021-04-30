package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.inventory.GlobalItems;
import me.RonanCraft.Pueblos.player.data.PlayerData;
import me.RonanCraft.Pueblos.player.data.PlayerInfoHandler;
import me.RonanCraft.Pueblos.player.events.EventListener;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.database.DatabaseClaims;
import org.bukkit.entity.Player;

public class Systems {

    private final Settings settings = new Settings();
    private final DatabaseClaims claimDatabase = new DatabaseClaims();
    private final ClaimHandler claim = new ClaimHandler();
    private final EventListener events = new EventListener();
    private final PlayerInfoHandler playerInfo = new PlayerInfoHandler();
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

    public PlayerData getPlayerData(Player p) {
        return playerInfo.getData(p);
    }

    public GlobalItems getGlobalItems() {
        return globalItems;
    }

    public Settings getSettings() {
        return settings;
    }
}
