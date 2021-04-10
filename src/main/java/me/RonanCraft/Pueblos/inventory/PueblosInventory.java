package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public enum PueblosInventory {
    MEMBERS(new InventoryClaimMembers(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.MEMBER),
    MEMBER(new InventoryClaimMember(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER),
    CLAIM(new InventoryClaim(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.MEMBER),
    REQUESTING(new InventoryRequesting(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.NONE),
    REQUESTS(new InventoryRequests(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER),
    FLAGS(new InventoryClaimFlags(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER);

    private final PueblosInv inv;
    private final PermissionNodes permNode;
    private final CLAIM_PERMISSION_LEVEL claimLevel;

    PueblosInventory(PueblosInv inv, PermissionNodes permNode, CLAIM_PERMISSION_LEVEL claimLevel) {
        this.inv = inv;
        this.permNode = permNode;
        this.claimLevel = claimLevel;
    }

    public void open(Player p, Claim claim, boolean from_command) {
        if (isAllowed(p, claim))
            if (inv instanceof PueblosInv_Claim)
                Pueblos.getInstance().getSystems().getPlayerInfo().setInventory(p, ((PueblosInv_Claim) inv).open(p, claim), this, from_command);
            else
                Pueblos.getInstance().getLogger().severe(this.name() + " is not a claim type!");
    }

    public void open(Player p, ClaimMember member, boolean from_command) {
        if (isAllowed(p, member.claim))
            if (inv instanceof PueblosInv_Member)
                Pueblos.getInstance().getSystems().getPlayerInfo().setInventory(p, ((PueblosInv_Member) inv).open(p, member), this, from_command);
            else
                Pueblos.getInstance().getLogger().severe(this.name() + " is not a member type!");
    }

    public void open(Player p, List<Claim> requestable, boolean from_command) {
        if (inv instanceof PueblosInv_Requesting)
            Pueblos.getInstance().getSystems().getPlayerInfo().setInventory(p, ((PueblosInv_Requesting) inv).open(p, requestable), this, from_command);
        else
            Pueblos.getInstance().getLogger().severe(this.name() + " is not a member type!");
    }

    public void openCasted(Player p, Object obj) {
        if (obj instanceof ClaimMember)
            open(p, (ClaimMember) obj, false);
        else if (obj instanceof Claim)
            open(p, (Claim) obj, false);
    }

    public boolean isAllowed(Player p, Claim claim) {
        if (!Pueblos.getInstance().getPermissions().checkPerm(permNode.node, p)
                || !claim.checkPermLevel(p, claimLevel)) {
            p.sendMessage("Not allowed!");
            return false;
        }
        return true;
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
