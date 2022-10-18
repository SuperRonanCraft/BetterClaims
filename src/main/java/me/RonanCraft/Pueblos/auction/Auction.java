package me.RonanCraft.Pueblos.auction;

import me.RonanCraft.Pueblos.claims.ClaimData;

public class Auction {

    public ClaimData claimData;
    public int price;
    public long time;
    public long auctionId;

    public Auction(ClaimData claimData, int price, long time) {
        this.claimData = claimData;
        this.price = price;
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public long getTime() {
        return time;
    }

}
