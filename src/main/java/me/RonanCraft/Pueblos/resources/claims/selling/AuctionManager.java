package me.RonanCraft.Pueblos.resources.claims.selling;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.Pueblos.resources.database.DatabaseAuctions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AuctionManager {

    private final ClaimHandler claimHandler;
    private final HashMap<Claim, Auction> auctions = new HashMap<>(); //Organized by claim to find auction faster

    public AuctionManager(ClaimHandler claimHandler) {
        this.claimHandler = claimHandler;
    }

    public void load() {
        this.auctions.clear();
        for (Auction auction : getDatabase().getAuctions()) {
            this.auctions.put(auction.claim, auction);
        }
    }

    private DatabaseAuctions getDatabase() {
        return Pueblos.getInstance().getDatabaseAuctions();
    }

    public CLAIM_ERRORS deleteAuction(@Nonnull Auction auction) {
        if (this.auctions.containsKey(auction.claim) && getDatabase().deleteAuction(auction)) {
            this.auctions.remove(auction.claim);
            return CLAIM_ERRORS.NONE;
        }
        return CLAIM_ERRORS.DATABASE_ERROR;
    }

    public CLAIM_ERRORS createAuction(@Nonnull Claim claim, int price, long time) {
        if (auctions.containsKey(claim)) {
            return CLAIM_ERRORS.AUCTION_EXISTS;
        }
        //Add some magic code to create an auction
        Auction auction = new Auction(claim, price, time);
        if (getDatabase().createAuction(auction)) {
            auctions.put(claim, auction);
            return CLAIM_ERRORS.NONE;
        } else
            return CLAIM_ERRORS.DATABASE_ERROR;
    }

    public Auction loadAuction(@Nonnull ResultSet result) throws SQLException {
        try {
            Claim claim = claimHandler.getClaim(result.getInt(DatabaseAuctions.COLUMNS.CLAIM_ID.name));
            int price = result.getInt(DatabaseAuctions.COLUMNS.PRICE.name);
            long time = result.getLong(DatabaseAuctions.COLUMNS.HOURS.name);
            Auction auction = new Auction(claim, price, time);
            auction.auctionId = result.getInt(DatabaseAuctions.COLUMNS.AUCTION_ID.name);
            return auction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public Auction getAuction(Claim claim) {
        return auctions.getOrDefault(claim, null);
    }
}
