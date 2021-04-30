package me.RonanCraft.Pueblos.player.data;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class PlayerInfoHandler {
    private final HashMap<Player, PlayerData> data = new HashMap<>();

    @Nonnull
    public PlayerData getData(Player p) {
        if (!data.containsKey(p))
            data.put(p, new PlayerData());
        return data.get(p);
    }

    public void clear() {
        data.clear();
    }

    public void clear(Player p) {
        data.remove(p);
    }
}
