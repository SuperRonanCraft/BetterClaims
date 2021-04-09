package me.RonanCraft.Pueblos.player;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.inventory.PueblosItem;
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
    private final HashMap<Player, List<PueblosInventory>> previous = new HashMap<>();
    private final HashMap<Player, PueblosInventory> current = new HashMap<>();

    public void addVisualization(Player p, Visualization vis) {
        visualization.put(p, vis);
    }

    public Visualization getVisualization(Player p) {
        return visualization.getOrDefault(p, null);
    }

    public void removeVisualization(Player p) {
        visualization.remove(p);
    }

    public void setInventory(Player p, Inventory inv, PueblosInventory pinv, boolean from_command) {
        if (from_command)
            clear(p);
        inventory.put(p, inv);
        current.put(p, pinv);
        addPrevious(p, pinv);
    }

    private void addPrevious(Player p, PueblosInventory pinv) {
        List<PueblosInventory> invs;
        if (previous.containsKey(p)) {
            invs = previous.get(p);
        } else
            invs = new ArrayList<>();
        if (!invs.contains(pinv))
            invs.add(pinv);
        previous.put(p, invs);
    }

    public void removePrevious(Player p) {
        if (previous.containsKey(p)) {
            List<PueblosInventory> invs = previous.get(p);
            invs.remove(invs.size() - 1);
            invs.remove(invs.size() - 1);
            previous.put(p, invs);
        }
    }

    public Inventory getInventory(Player p) {
        return inventory.getOrDefault(p, null);
    }

    public PueblosInventory getCurrent(Player p) {
        return current.getOrDefault(p, null);
    }

    public PueblosInventory getPrevious(Player p, PueblosInventory pinv) {
        PueblosInventory previous = null;
        if (this.previous.containsKey(p) && !this.previous.get(p).isEmpty())
            previous = this.previous.get(p).get(this.previous.get(p).size() - 1);
        if (previous == pinv && this.previous.get(p).size() > 1)
            previous = this.previous.get(p).get(this.previous.get(p).size() - 2);
        return previous;
    }

    public void clearInventory(Player p) {
        inventory.remove(p);
        current.remove(p);
    }

    public void clear() {
        visualization.clear();
        overriding.clear();
        inventory.clear();
        current.clear();
    }

    private void clear(Player p) {
        inventory.remove(p);
        current.remove(p);
        previous.remove(p);
    }
}
