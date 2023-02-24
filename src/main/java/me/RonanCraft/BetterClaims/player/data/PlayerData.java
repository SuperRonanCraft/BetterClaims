package me.RonanCraft.BetterClaims.player.data;

import lombok.Getter;
import lombok.Setter;
import me.RonanCraft.BetterClaims.inventory.PueblosInventory;
import me.RonanCraft.BetterClaims.player.events.PlayerClaimInteraction;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    @Getter @Setter private @Nullable Visualization visualization;
    @Getter private boolean overriding;
    @Getter private @Nullable Inventory inventory;
    private List<PueblosInventory> previous = new ArrayList<>();
    @Getter private @Nullable PueblosInventory currentInventory;
    @Getter @Setter private @Nullable PlayerClaimInteraction claimInteraction;
    @Getter @Setter private Claim insideClaim;

    public void removeVisualization() {
        visualization = null;
    }

    //Inventory Menus
    public void setInventory(Inventory inv, PueblosInventory pinv, boolean from_command) {
        if (from_command)
            clear();
        inventory = inv;
        currentInventory = pinv;
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
        currentInventory = null;
    }

    //Overrides
    public void toggleOverride() {
        overriding = !overriding;
    }

    public void removeClaimInteraction() {
        claimInteraction = null;
    }

    public void clear() {
        visualization = null;
        overriding = false;
        inventory = null;
        currentInventory = null;
    }

    public void removeInsideClaim() {
        setInsideClaim(null);
    }
}
