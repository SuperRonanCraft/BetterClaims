package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.event.Cancellable;

public abstract class ClaimEventType_ClaimCancellable extends ClaimEventType_Claim implements Cancellable {

    boolean cancelled;
    boolean sendCancelledMessage = true;

    ClaimEventType_ClaimCancellable(ClaimData claimData, boolean async) {
        super(claimData, async);
    }

    public void setSendCancelledMessage(boolean b) {
        sendCancelledMessage = b;
    }

    public boolean sendCancelledMessage() {
        return sendCancelledMessage;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
