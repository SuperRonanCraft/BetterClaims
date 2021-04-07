package me.RonanCraft.Pueblos.player;

import me.RonanCraft.Pueblos.resources.tools.visual.Visualization;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerInfo {
    private final HashMap<Player, Visualization> visualization = new HashMap<>();

    public void addVisualization(Player p, Visualization vis) {
        visualization.put(p, vis);
    }

    public Visualization getVisualization(Player p) {
        return visualization.getOrDefault(p, null);
    }

    public void removeVisualization(Player p) {
        visualization.remove(p);
    }
}
