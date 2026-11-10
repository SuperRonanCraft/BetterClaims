package me.RonanCraft.BetterClaims.player.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PlayerInfoHandler {
    private final java.util.concurrent.ConcurrentMap<Player, PlayerData> data = new java.util.concurrent.ConcurrentHashMap<>();

    @NotNull
    public PlayerData getData(Player p) {
        return data.computeIfAbsent(p, ignored -> new PlayerData());
    }

    public void clear() {
        data.clear();
    }

    public void clear(Player p) {
        data.remove(p);
    }
}
