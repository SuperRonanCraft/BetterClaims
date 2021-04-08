package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public enum PueblosInventory {
    MEMBERS(new InventoryClaimMembers()),
    MEMBER(new InventoryClaimMember()),
    CLAIM(new InventoryClaim());

    private final PueblosInv inv;

    PueblosInventory(PueblosInv inv) {
        this.inv = inv;
    }

    public void open(Player p, Claim claim) {
        if (inv instanceof PueblosInv_Claim)
            Pueblos.getInstance().getSystems().getPlayerInfo().addInventory(p, ((PueblosInv_Claim) inv).open(p, claim), this);
        else
            Pueblos.getInstance().getLogger().severe(this.name() + " is not a claim type!");
    }

    public void open(Player p, ClaimMember member) {
        if (inv instanceof PueblosInv_Member)
            Pueblos.getInstance().getSystems().getPlayerInfo().addInventory(p, ((PueblosInv_Member) inv).open(p, member), this);
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
