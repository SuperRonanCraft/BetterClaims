package me.RonanCraft.Pueblos.player;

import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerInfo {
    private final HashMap<Player, Visualization> visualization = new HashMap<>();
    private final HashMap<Player, Boolean> overriding = new HashMap<>();
    private final HashMap<Player, Inventory> inventory = new HashMap<>();
    private final HashMap<Player, PueblosInventory> inventoryPlugin = new HashMap<>();
    private final HashMap<Player, List<PueblosInventory>> previousPlugin = new HashMap<>();

    public void addVisualization(Player p, Visualization vis) {
        visualization.put(p, vis);
    }

    public Visualization getVisualization(Player p) {
        return visualization.getOrDefault(p, null);
    }

    public void removeVisualization(Player p) {
        visualization.remove(p);
    }

    public void addInventory(Player p, Inventory inv, PueblosInventory pinv) {
        inventory.put(p, inv);
        inventoryPlugin.put(p, pinv);
        List<PueblosInventory> invs;
        if (previousPlugin.containsKey(p)) {
            invs = previousPlugin.get(p);
        } else
            invs = new ArrayList<>();
        invs.add(0, pinv);
        previousPlugin.put(p, invs);
    }

    public Inventory getInventory(Player p) {
        return inventory.getOrDefault(p, null);
    }

    public PueblosInventory getInventoryPlugin(Player p) {
        return inventoryPlugin.getOrDefault(p, null);
    }

    public PueblosInventory getPreviousPlugin(Player p, boolean remove) {
        PueblosInventory previous = null;
        if (previousPlugin.containsKey(p) && !previousPlugin.get(p).isEmpty()) {
            previous = previousPlugin.get(p).get(0);
            if (remove)
                previousPlugin.get(p).remove(0);
        }
        return previous;
    }

    public void clearInventory(Player p) {
        inventory.remove(p);
        inventoryPlugin.remove(p);
    }

    public void clear() {
        visualization.clear();
        overriding.clear();
        inventory.clear();
        inventoryPlugin.clear();
    }
}
