package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimInfo;
import org.bukkit.event.Cancellable;

abstract class PueblosEventType_ClaimCancellable extends PueblosEventType_Claim implements Cancellable {

    boolean cancelled;

    PueblosEventType_ClaimCancellable(ClaimInfo claim) {
        super(claim);
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
