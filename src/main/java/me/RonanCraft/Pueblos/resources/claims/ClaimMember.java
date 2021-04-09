package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class ClaimMember {

    public final UUID uuid;
    public final String name;
    public final boolean owner;
    public final Date date;
    private final HashMap<CLAIM_FLAG_MEMBER, Object> flags = new HashMap<>();
    public final Claim claim;

    public ClaimMember(UUID uuid, String name, Date date, boolean owner, Claim claim) {
        this.uuid = uuid;
        this.name = name;
        this.date = date;
        this.owner = owner;
        this.claim = claim;
    }

    public HashMap<CLAIM_FLAG_MEMBER, Object> getFlags() {
        return flags;
    }

    public void setFlag(CLAIM_FLAG_MEMBER flag, Object value, boolean updated) {
        if (updated && flags.containsKey(flag) && !flags.get(flag).equals(value))
            claim.updated();
        flags.put(flag, value);
    }

    public void setFlag(HashMap<CLAIM_FLAG_MEMBER, Object> values, boolean updated) {
        for (Map.Entry<CLAIM_FLAG_MEMBER, Object> flag : values.entrySet())
            setFlag(flag.getKey(), flag.getValue(), updated);
    }

    public UUID getId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
}
