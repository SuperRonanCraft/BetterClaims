package me.RonanCraft.BetterClaims.auction;

import lombok.Getter;
import me.RonanCraft.BetterClaims.claims.ClaimData;

public class Auction {

    public ClaimData claimData;
    @Getter public int price;
    @Getter public long time;
    public long auctionId;

    public Auction(ClaimData claimData, int price, long time) {
        this.claimData = claimData;
        this.price = price;
        this.time = time;
    }

}
