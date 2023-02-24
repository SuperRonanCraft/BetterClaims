package me.RonanCraft.BetterClaims.player.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PlayerInfoHandler {
    private final HashMap<Player, PlayerData> data = new HashMap<>();

    @NotNull
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
