/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import java.util.ArrayList;
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

    public ClaimMember getMember(UUID id) {
        ClaimMember member = null;
        for (ClaimMember _member : members)
            if (_member.uuid.equals(id))
                member = _member;
        return member;
    }

    public List<ClaimMember> getMembers() {
        return members;
    }

    public boolean isMember(UUID id) {
        if (id.equals(claim.getOwnerID()))
            return true;
        for (ClaimMember member : members)
            if (member.uuid.equals(id))
                return true;
        return false;
    }

    public void remove(ClaimMember member, boolean update) {
        members.remove(member);
        if (update)
            claim.updated();
    }
}
