package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimMembers implements PueblosInv_Claim {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Claim> claim = new HashMap<>();

    @Override
    public Inventory open(Player p, Claim claim) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "Members " + claim.getName());

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();

        PueblosInventory pinv = getPl().getSystems().getPlayerInfo().getPreviousPlugin(p, false);
        if (pinv != null) {
            int slot = inv.firstEmpty();
            ItemStack item = new ItemStack(Material.ARROW);
            setTitle(item, p, "<- &eBack");
            List<String> lore = new ArrayList<>();
            lore.add("Click to go back to " + pinv.name());
            setLore(item, p, lore);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.BACK, pinv, claim));
        }

        int slot = 18;
        for (ClaimMember member : claim.getMembers()) {
            slot++;
            while (slot < inv.getSize() && inv.getItem(slot) != null)
                slot++;
            if (slot >= inv.getSize())
                break;
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            setTitle(item, member.getPlayer(), "&7" + member.name);
            List<String> lore = new ArrayList<>();
            lore.add("Owner: " + member.owner);
            if (!member.getFlags().isEmpty()) {
                lore.add("Flags");
                member.getFlags().forEach((flag, value) -> {
                    lore.add(" - " + flag.name() + ": " + value.toString());
                });
            }
            setLore(item, p, lore);
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, member));
        }
        this.itemInfo.put(p, itemInfo);
        this.claim.put(p, claim);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!itemInfo.containsKey(p) || !claim.containsKey(p) || checkItems(e, itemInfo.get(p)))
            return;

        if (!claim.get(p).isOwner(p)) {
            Messages.core.sms(p, "Not enough permissions!");
            return;
        }

        ClaimMember member = (ClaimMember) itemInfo.get(p).get(e.getSlot()).info;
        //this.itemInfo.remove(p);
        PueblosInventory.MEMBER.open(p, member);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
    }
}
