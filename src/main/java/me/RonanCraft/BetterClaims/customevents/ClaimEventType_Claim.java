package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class ClaimEventType_Claim extends Event {

    private static final HandlerList handler = new HandlerList();

    ClaimData claimData;

    ClaimEventType_Claim(ClaimData claimData, boolean async) {
        super(async);
        this.claimData = claimData;
    }

    public ClaimData getClaim() {
        return claimData;
    }

    //Default Stuff
    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handler;
    }

}
