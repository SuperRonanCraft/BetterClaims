package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PueblosEvent_ClaimCreate extends ClaimEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player creator;

    /**
     * Broadcasts this new claim that was created
     * @param claim The claim that was just created
     * @param creator The player who created this claim
     */
    public PueblosEvent_ClaimCreate(Claim claim, Player creator) {
        super(claim);
        this.creator = creator;
    }

    //Required
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getCreator() {
        return creator;
    }
}
