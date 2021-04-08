package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.Systems;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public enum PueblosInventory {
    MEMBERS(new InventoryClaimMembers());

    private final PueblosInv inv;

    PueblosInventory(PueblosInv inv) {
        this.inv = inv;
    }

    public void open(Player p, Claim claim) {
        if (inv instanceof PueblosInv_ClaimInfo)
            Pueblos.getInstance().getSystems().getPlayerInfo().addInventory(p, ((PueblosInv_ClaimInfo) inv).open(p, claim), this);
        else
            Pueblos.getInstance().getLogger().severe(this.name() + " is not a claim type!");
    }

    public void click(InventoryClickEvent e) {
        inv.clickEvent(e);
    }

    public void closeEvent(Player p) {
        //TODO: Close event code
    }
}
