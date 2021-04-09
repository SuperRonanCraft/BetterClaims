package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public enum PueblosInventory {
    MEMBERS(new InventoryClaimMembers()),
    MEMBER(new InventoryClaimMember()),
    CLAIM(new InventoryClaim()),
    REQUESTING(new InventoryRequesting()),
    FLAGS(new InventoryClaimFlags());

    private final PueblosInv inv;

    PueblosInventory(PueblosInv inv) {
        this.inv = inv;
    }

    public void open(Player p, Claim claim, boolean from_command) {
        if (inv instanceof PueblosInv_Claim)
            Pueblos.getInstance().getSystems().getPlayerInfo().setInventory(p, ((PueblosInv_Claim) inv).open(p, claim), this, from_command);
        else
            Pueblos.getInstance().getLogger().severe(this.name() + " is not a claim type!");
    }

    public void open(Player p, ClaimMember member, boolean from_command) {
        if (inv instanceof PueblosInv_Member)
            Pueblos.getInstance().getSystems().getPlayerInfo().setInventory(p, ((PueblosInv_Member) inv).open(p, member), this, from_command);
        else
            Pueblos.getInstance().getLogger().severe(this.name() + " is not a member type!");
    }

    public void open(Player p, List<Claim> requestable, boolean from_command) {
        if (inv instanceof PueblosInv_Requests)
            Pueblos.getInstance().getSystems().getPlayerInfo().setInventory(p, ((PueblosInv_Requests) inv).open(p, requestable), this, from_command);
        else
            Pueblos.getInstance().getLogger().severe(this.name() + " is not a member type!");
    }

    public void openCasted(Player p, Object obj) {
        if (obj instanceof ClaimMember)
            open(p, (ClaimMember) obj, false);
        else if (obj instanceof Claim)
            open(p, (Claim) obj, false);
    }

    public void click(InventoryClickEvent e) {
        inv.clickEvent(e);
    }

    public void closeEvent(Player p) {
        //Pueblos.getInstance().getSystems().getPlayerInfo().addPrevious(p, this);
    }

    public PueblosInv get() {
        return inv;
    }
}
