package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.event.Cancellable;

public abstract class PueblosEventType_ClaimCancellable extends PueblosEventType_Claim implements Cancellable {

    boolean cancelled;
    boolean sendCancelledMessage = true;

    PueblosEventType_ClaimCancellable(Claim claim) {
        super(claim);
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
