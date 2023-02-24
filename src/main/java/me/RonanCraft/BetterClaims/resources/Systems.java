package me.RonanCraft.BetterClaims.resources;

import me.RonanCraft.BetterClaims.inventory.GlobalItems;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.player.data.PlayerInfoHandler;
import me.RonanCraft.BetterClaims.player.events.EventListener;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.database.DatabaseAuctions;
import me.RonanCraft.BetterClaims.database.DatabaseClaims;
import org.bukkit.entity.Player;

public class Systems {

    private final Settings settings = new Settings();
    private final DatabaseClaims databaseClaims = new DatabaseClaims();
    private final DatabaseAuctions databaseAuctions = new DatabaseAuctions();
    private final ClaimHandler claim = new ClaimHandler();
    private final EventListener events = new EventListener();
    private final PlayerInfoHandler playerInfo = new PlayerInfoHandler();
    private final GlobalItems globalItems = new GlobalItems();

    public void load(boolean reload) {
        settings.load(); //Load first
        databaseClaims.load();
        databaseAuctions.load();
        claim.load();
        playerInfo.clear();
        globalItems.load();
        events.load(reload);
    }

    public DatabaseClaims getDatabaseClaims() {
        return databaseClaims;
    }

    public DatabaseAuctions getDatabaseAuctions() {
        return databaseAuctions;
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
