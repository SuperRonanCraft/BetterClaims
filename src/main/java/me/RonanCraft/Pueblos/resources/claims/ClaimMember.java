package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class ClaimMember {

    public final UUID uuid;
    public final String name;
    public final boolean owner;
    public final Date date;
    public HashMap<CLAIM_FLAG_MEMBER, Object> flags = new HashMap<>();
    public final Claim claim;

    public ClaimMember(UUID uuid, String name, Date date, boolean owner, Claim claim) {
        this.uuid = uuid;
        this.name = name;
        this.date = date;
        this.owner = owner;
        this.claim = claim;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
