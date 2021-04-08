package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryClaimMember implements PueblosInv_Member {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, ClaimMember> member = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimMember member) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "Member " + member.name);

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
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.BACK, pinv, member));
        }

        int slot = 13;
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        setTitle(item, p, "&7" + member.name);
        List<String> lore = new ArrayList<>();
        lore.add("Pueblo: " + member.claim.getName());
        setLore(item, p, lore);
        inv.setItem(slot, item);

        //Flags
        slot = slot + 5;
        for (CLAIM_FLAG_MEMBER flag : CLAIM_FLAG_MEMBER.values()) {
            item = new ItemStack(Material.GOLD_INGOT);
            setTitle(item, p, StringUtils.capitalize(flag.name().replaceAll("_", " ").toLowerCase()));
            Object value = flag.getDefault();
            if (member.getFlags().containsKey(flag))
                value = member.getFlags().get(flag);
            lore = new ArrayList<>();
            lore.add(StringUtils.capitalize(value.toString().toLowerCase()));
            setLore(item, p, lore);
            slot++;
            while (slot < inv.getSize() && inv.getItem(slot) != null)
                slot++;
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, flag));
        }
        this.itemInfo.put(p, itemInfo);
        this.member.put(p, member);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!itemInfo.containsKey(p) || checkItems(e, itemInfo.get(p)))
            return;

        CLAIM_FLAG_MEMBER flag = (CLAIM_FLAG_MEMBER) itemInfo.get(p).get(e.getSlot()).info;
        ClaimMember member = this.member.get(p);
        Object current_value = member.getFlags().getOrDefault(flag, flag.getDefault());
        this.member.get(p).setFlag(flag, flag.alter(current_value), true);
        PueblosInventory.MEMBER.open(p, this.member.get(p));
        //this.itemInfo.remove(p);
        //this.member.remove(p);
    }

    @Override
    public void clear(Player p) {
        itemInfo.remove(p);
        member.remove(p);
    }
}
