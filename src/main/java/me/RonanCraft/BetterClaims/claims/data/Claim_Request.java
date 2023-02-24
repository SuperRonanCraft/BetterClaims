/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims.data;

import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.resources.helper.HelperDate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.UUID;

public class Claim_Request {
    public final UUID id;
    public final String name;
    public final Date date;
    public final ClaimData claimData;

    public Claim_Request(UUID id, String name, Date date, ClaimData claimData) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.claimData = claimData;
    }


    public void accepted() {
        claimData.addMember(new Member(id, name, HelperDate.getDate(), claimData), true);
        claimData.removeRequest(this, true);
    }

    public void declined() {
        claimData.removeRequest(this, true);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(id);
    }
}
