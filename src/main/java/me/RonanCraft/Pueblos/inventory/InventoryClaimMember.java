package me.RonanCraft.Pueblos.inventory;

import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.Claim;
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

    private final HashMap<Player, HashMap<Integer, CLAIM_FLAG_MEMBER>> flag_map = new HashMap<>();
    private final HashMap<Player, ClaimMember> member = new HashMap<>();

    @Override
    public Inventory open(Player p, ClaimMember member) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "Member " + member.name);

        addBorder(inv);

        int slot = inv.firstEmpty() + 3;
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        setTitle(item, p, "&7" + member.name);
        List<String> lore = new ArrayList<>();
        lore.add("Pueblo: " + member.claim.getName());
        setLore(item, p, lore);
        inv.setItem(slot, item);

        //Flags
        slot = slot + 5;
        HashMap<Integer, CLAIM_FLAG_MEMBER> itemSlots = new HashMap<>();
        for (CLAIM_FLAG_MEMBER flag : CLAIM_FLAG_MEMBER.values()) {
            item = new ItemStack(Material.GOLD_INGOT);
            setTitle(item, p, StringUtils.capitalize(flag.name().replaceAll("_", " ").toLowerCase()));
            Object value = flag.getDefault();
            if (member.flags.containsKey(flag))
                value = member.flags.get(flag);
            lore = new ArrayList<>();
            lore.add(StringUtils.capitalize(value.toString().toLowerCase()));
            setLore(item, p, lore);
            slot++;
            while (inv.getItem(slot) != null && slot < inv.getSize())
                slot++;
            inv.setItem(slot, item);
            itemSlots.put(slot, flag);
        }
        this.flag_map.put(p, itemSlots);
        this.member.put(p, member);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (flag_map.containsKey(p)) {
            CLAIM_FLAG_MEMBER flag = flag_map.get(p).get(e.getSlot());
            this.member.get(p).flags.put(flag, flag.cast(flag))
            this.open(p, this.member.get(p));
            this.flag_map.remove(p);
            this.member.remove(p);
        }
    }

}
