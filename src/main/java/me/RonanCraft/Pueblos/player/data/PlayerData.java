package me.RonanCraft.Pueblos.player.data;

import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.events.PlayerClaimInteraction;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerData {
    private Visualization visualization;
    private boolean overriding;
    private Inventory inventory;
    private List<PueblosInventory> previous = new ArrayList<>();
    private PueblosInventory pueblosInventory;
    private PlayerClaimInteraction claimInteraction;
    private Claim insideClaim;

    //Visualization
    @Nullable
    public Visualization getVisualization() {
        return visualization;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public void removeVisualization() {
        visualization = null;
    }

    //Inventory Menus
    public void setInventory(Inventory inv, PueblosInventory pinv, boolean from_command) {
        if (from_command)
            clear();
        inventory = inv;
        pueblosInventory = pinv;
        addPrevious(pinv);
    }

    private void addPrevious(PueblosInventory pinv) {
        List<PueblosInventory> invs = previous;
        if (!invs.contains(pinv))
            invs.add(pinv);
        previous = invs;
    }

    public void removePrevious() {
        if (!previous.isEmpty()) {
            List<PueblosInventory> invs = previous;
            invs.remove(invs.size() - 1);
            invs.remove(invs.size() - 1);
            previous = invs;
        }
    }

    @Nullable
    public Inventory getInventory() {
        return inventory;
    }

    @Nullable
    public PueblosInventory getCurrent() {
        return pueblosInventory;
    }

    @Nullable
    public PueblosInventory getPrevious(PueblosInventory pinv) {
        PueblosInventory previous = null;
        if (!this.previous.isEmpty())
            previous = this.previous.get(this.previous.size() - 1);
        if (previous == pinv && this.previous.size() > 1)
            previous = this.previous.get(this.previous.size() - 2);
        return previous;
    }

    public void clearInventory() {
        inventory = null;
        pueblosInventory = null;
    }

    //Overrides
    public void toggleOverride() {
        overriding = !overriding;
    }

    //Claim Interaction
    public void setClaimInteraction(PlayerClaimInteraction claimInteraction) {
        this.claimInteraction = claimInteraction;
    }

    @Nullable
    public PlayerClaimInteraction getClaimInteraction() {
        return claimInteraction;
    }

    public void removeClaimInteraction() {
        claimInteraction = null;
    }

    public boolean isOverriding() {
        return overriding;
    }

    public void clear() {
        visualization = null;
        overriding = false;
        inventory = null;
        pueblosInventory = null;
    }

    //inside Claim
    @Nullable
    public Claim getInsideClaim() {
        return insideClaim;
    }

    public void setInsideClaim(Claim claim) {
        insideClaim = claim;
    }

    public void removeInsideClaim() {
        setInsideClaim(null);
    }
}
