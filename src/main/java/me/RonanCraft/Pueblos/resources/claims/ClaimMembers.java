package me.RonanCraft.Pueblos.resources.claims;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClaimMembers {
    private final List<ClaimMember> members = new ArrayList<>();

    private final Claim claim;

    ClaimMembers(Claim claim) {
        this.claim = claim;
    }

    public void addMember(ClaimMember member, boolean update) {
        members.add(member);
        if (update)
            claim.updated();
    }

    public ClaimMember getMember(Player p) {
        ClaimMember member = null;
        for (ClaimMember _member : members)
            if (_member.uuid.equals(p.getUniqueId()))
                member = _member;
        return member;
    }

    public List<ClaimMember> getMembers() {
        return members;
    }

    public boolean isMember(Player p) {
        if (p.getUniqueId().equals(claim.ownerId))
            return true;
        for (ClaimMember member : members)
            if (member.uuid.equals(p.getUniqueId()))
                return true;
        return false;
    }

    public void remove(ClaimMember member, boolean update) {
        members.remove(member);
        if (update)
            claim.updated();
    }
}
