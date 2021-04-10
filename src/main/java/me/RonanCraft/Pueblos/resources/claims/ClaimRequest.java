package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.UUID;

public class ClaimRequest {
    public final UUID id;
    public final String name;
    public final Date date;
    public final Claim claim;

    public ClaimRequest(UUID id, String name, Date date, Claim claim) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.claim = claim;
    }


    public void accepted() {
        claim.addMember(new ClaimMember(id, name, HelperDate.getDate(), false, claim), true);
        claim.removeRequest(this, true);
    }

    public void declined() {
        claim.removeRequest(this, true);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(id);
    }
}
