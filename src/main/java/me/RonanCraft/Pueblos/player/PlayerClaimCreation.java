package me.RonanCraft.Pueblos.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerClaimCreation {

    private final Player player;
    final List<Location> locations = new ArrayList<>();
    boolean locked = false;

    PlayerClaimCreation(Player player) {
        this.player = player;
    }

    boolean addLocation(Location loc) {
        if (locations.contains(loc)) {
            return false;
        } else {
            locations.add(loc);
        }
        if (locations.size() > 2)
            locations.remove(0);
        return true;
    }

    void lock() {
        locked = true;
    }
}
