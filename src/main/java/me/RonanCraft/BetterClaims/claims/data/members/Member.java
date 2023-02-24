/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims.data.members;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG_MEMBER;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Member {

    public final UUID uuid;
    public final String name;
    public final Date date;
    private final HashMap<CLAIM_FLAG_MEMBER, Object> flags = new HashMap<>();
    public final ClaimData claimData;

    public Member(UUID uuid, String name, Date date, ClaimData claimData) {
        this.uuid = uuid;
        this.name = name;
        this.date = date;
        this.claimData = claimData;
    }

    public HashMap<CLAIM_FLAG_MEMBER, Object> getFlags() {
        return flags;
    }

    public void setFlag(CLAIM_FLAG_MEMBER flag, Object value, boolean updated) {
        if (updated && flags.containsKey(flag) && !flags.get(flag).equals(value))
            claimData.updated(true);
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
