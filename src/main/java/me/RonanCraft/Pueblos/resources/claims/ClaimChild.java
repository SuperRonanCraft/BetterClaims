/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ClaimChild extends Claim {
    public final ClaimMain parent;

    ClaimChild(BoundingBox boundingBox, ClaimMain parent) {
        super(parent.ownerId, parent.ownerName, boundingBox);
        this.parent = parent;
    }

    @Override
    public ClaimFlags getFlags() { //Return the parents flags if childs flags are empty
        if (super.getFlags().flags.isEmpty())
            return getParent().getFlags();
        return super.getFlags();
    }

    @Override
    public List<ClaimMember> getMembers() {
        List<ClaimMember> list = new ArrayList<>(super.getMembers());
        for (ClaimMember pMember : getParent().getMembers()) {
            boolean add = true;
            for (ClaimMember cMember : list)
                if (cMember.getId().equals(pMember.getId())) {
                    add = false;
                    break;
                }
            if (add) //Members of sub claims are higher priority than parents members
                list.add(pMember);
        }
        return list;
    }

    @Nonnull
    public ClaimMain getParent() {
        return this.parent;
    }

    public boolean isChild() {
        return parent != null;
    }
}
