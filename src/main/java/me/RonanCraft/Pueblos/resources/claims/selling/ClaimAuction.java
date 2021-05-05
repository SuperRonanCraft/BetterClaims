package me.RonanCraft.Pueblos.resources.claims.selling;

import me.RonanCraft.Pueblos.resources.claims.ClaimMain;

public class ClaimAuction {

    public ClaimMain claim;
    public int price;
    public long time;
    public long auctionId;

    public ClaimAuction(ClaimMain claim, int price, long time) {
        this.claim = claim;
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
