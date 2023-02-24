package me.RonanCraft.BetterClaims.auction;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.BetterClaims.database.DatabaseAuctions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AuctionManager {

    private final ClaimHandler claimHandler;
    private final HashMap<ClaimData, Auction> auctions = new HashMap<>(); //Organized by claim to find auction faster

    public AuctionManager(ClaimHandler claimHandler) {
        this.claimHandler = claimHandler;
    }

    public void load() {
        this.auctions.clear();
        for (Auction auction : getDatabase().getAuctions()) {
            this.auctions.put(auction.claimData, auction);
        }
    }

    private DatabaseAuctions getDatabase() {
        return BetterClaims.getInstance().getDatabaseAuctions();
    }

    public CLAIM_ERRORS deleteAuction(@NotNull Auction auction) {
        if (this.auctions.containsKey(auction.claimData) && getDatabase().deleteAuction(auction)) {
            this.auctions.remove(auction.claimData);
            return CLAIM_ERRORS.NONE;
        }
        return CLAIM_ERRORS.DATABASE_ERROR;
    }

    public CLAIM_ERRORS createAuction(@NotNull ClaimData claimData, int price, long time) {
        if (auctions.containsKey(claimData)) {
            return CLAIM_ERRORS.AUCTION_EXISTS;
        }
        //Add some magic code to create an auction
        Auction auction = new Auction(claimData, price, time);
        if (getDatabase().createAuction(auction)) {
            auctions.put(claimData, auction);
            return CLAIM_ERRORS.NONE;
        } else
            return CLAIM_ERRORS.DATABASE_ERROR;
    }

    public Auction loadAuction(@NotNull ResultSet result) throws SQLException {
        try {
            ClaimData claimData = claimHandler.getClaim(result.getInt(DatabaseAuctions.COLUMNS.CLAIM_ID.name));
            int price = result.getInt(DatabaseAuctions.COLUMNS.PRICE.name);
            long time = result.getLong(DatabaseAuctions.COLUMNS.HOURS.name);
            Auction auction = new Auction(claimData, price, time);
            auction.auctionId = result.getInt(DatabaseAuctions.COLUMNS.AUCTION_ID.name);
            return auction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public Auction getAuction(ClaimData claimData) {
        return auctions.getOrDefault(claimData, null);
    }
}
