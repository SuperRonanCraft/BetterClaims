package me.RonanCraft.BetterClaims.inventory;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.inventory.types.*;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_PERMISSION_LEVEL;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public enum ClaimInventory {
    MEMBERS(new InventoryClaimMembers(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.MEMBER),
    MEMBER(new InventoryClaimMember(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER),
    CLAIM(new InventoryClaim(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.MEMBER),
    CLAIM_SELECT(new InventoryClaimSelect(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER),
    REQUESTING_ALL(new InventoryRequesting_Player(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.NONE),
    REQUESTING_PLAYER(new InventoryRequesting_Player(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.NONE),
    REQUESTS(new InventoryRequests(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER),
    FLAGS(new InventoryClaimFlags(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.OWNER),
    CONFIRM(new InventoryConfirm(), PermissionNodes.USE, CLAIM_PERMISSION_LEVEL.NONE);

    private final ClaimInv inv;
    private final PermissionNodes permNode;
    private final CLAIM_PERMISSION_LEVEL claimLevel;

    ClaimInventory(ClaimInv inv, PermissionNodes permNode, CLAIM_PERMISSION_LEVEL claimLevel) {
        this.inv = inv;
        this.permNode = permNode;
        this.claimLevel = claimLevel;
    }

    public void open(Player p, ClaimData claimData, boolean from_command) {
        if (isAllowed(p, claimData))
            if (inv instanceof ClaimInv_Claim)
                HelperPlayer.getData(p).setInventory(((ClaimInv_Claim) inv).open(p, claimData), this, from_command);
            else
                BetterClaims.getInstance().getLogger().severe(this.name() + " is not a claim type!");
    }

    public void open(Player p, Member member, boolean from_command) {
        if (isAllowed(p, member.claimData))
            if (inv instanceof ClaimInv_Member)
                HelperPlayer.getData(p).setInventory(((ClaimInv_Member) inv).open(p, member), this, from_command);
            else
                BetterClaims.getInstance().getLogger().severe(this.name() + " is not a member type!");
    }

    public void open(Player p, List<ClaimData> claimData, boolean from_command) {
        if (inv instanceof ClaimInv_MultiClaim)
            HelperPlayer.getData(p).setInventory(((ClaimInv_MultiClaim) inv).open(p, claimData), this, from_command);
        else
            BetterClaims.getInstance().getLogger().severe(this.name() + " is not a request type!");
    }

    public void open(Player p, Confirmation confirmation, boolean from_command) {
        if (inv instanceof ClaimInv_Confirming)
            HelperPlayer.getData(p).setInventory(((ClaimInv_Confirming) inv).open(p, confirmation), this, from_command);
        else
            BetterClaims.getInstance().getLogger().severe(this.name() + " is not a confirm type!");
    }

    public void openCasted(Player p, Object obj) {
        if (obj instanceof Member)
            open(p, (Member) obj, false);
        else if (obj instanceof Claim)
            open(p, (Claim) obj, false);
        else if (obj instanceof Confirmation)
            open(p, (Confirmation) obj, false);
        else
            p.sendMessage("A wrong inventory happened!");
    }

    public boolean isAllowed(Player p, ClaimData claimData) {
        //p.sendMessage("Not allowed!");
        return BetterClaims.getInstance().getPermissions().checkPerm(permNode.node, p)
                && claimData.checkPermLevel(p, claimLevel);
    }

    public void click(InventoryClickEvent e) {
        inv.clickEvent(e);
    }

    public void closeEvent(Player p) {
        //BetterClaims.getInstance().getSystems().getPlayerInfo().addPrevious(p, this);
    }

    public ClaimInv get() {
        return inv;
    }
}
