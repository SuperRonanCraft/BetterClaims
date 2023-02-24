/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims.data.members;

import me.RonanCraft.BetterClaims.claims.ClaimData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimMembers {
    private final List<Member> members = new ArrayList<>();

    private final ClaimData claimData;

    public ClaimMembers(ClaimData claimData) {
        this.claimData = claimData;
    }

    public void addMember(Member member, boolean update) {
        members.add(member);
        claimData.updated(update);
    }

    public Member getMember(UUID id) {
        Member member = null;
        for (Member _member : members)
            if (_member.uuid.equals(id))
                member = _member;
        return member;
    }

    public List<Member> getMembers() {
        return members;
    }

    public boolean isMember(UUID id) {
        if (id.equals(claimData.getOwnerID()))
            return true;
        for (Member member : members)
            if (member.uuid.equals(id))
                return true;
        return false;
    }

    public void remove(Member member, boolean update) {
        members.remove(member);
        claimData.updated(update);
    }
}
