/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims;

import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.claims.data.Claim_FlagHandler;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Claim_Child extends ClaimData {
    public final Claim parent;

    public Claim_Child(BoundingBox boundingBox, @NotNull Claim parent) {
        super(parent.ownerId, parent.ownerName, boundingBox);
        this.parent = parent;
    }

    @Override
    public Claim_FlagHandler getFlags() { //Return the parents flags if childs flags are empty
        if (super.getFlags().flags.isEmpty())
            return getParent().getFlags();
        return super.getFlags();
    }

    @Override
    public List<Member> getMembers() {
        List<Member> list = new ArrayList<>(super.getMembers());
        for (Member pMember : getParent().getMembers()) {
            boolean add = true;
            for (Member cMember : list)
                if (cMember.getId().equals(pMember.getId())) {
                    add = false;
                    break;
                }
            if (add) //Members of sub claims are higher priority than parents members
                list.add(pMember);
        }
        return list;
    }

    @NotNull
    public Claim getParent() {
        return this.parent;
    }

    public boolean isChild() {
        return parent != null;
    }
}
