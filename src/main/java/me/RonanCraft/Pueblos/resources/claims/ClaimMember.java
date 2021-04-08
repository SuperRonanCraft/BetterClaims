package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class ClaimMember {

    public final UUID uuid;
    public final String name;
    public final boolean owner;
    public final Date date;

    public ClaimMember(UUID uuid, String name, Date date, boolean owner) {
        this.uuid = uuid;
        this.name = name;
        this.date = date;
        this.owner = owner;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
