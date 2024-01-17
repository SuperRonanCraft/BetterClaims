/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.claims.enums;

import lombok.Getter;
import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;

@Getter public enum CLAIM_FLAG {
    PVP(false),
    TNT(false),
    ALLOW_DOOR(false, CLAIM_FLAG_MEMBER.ALLOW_DOOR),
    ALLOW_LEVER(false, CLAIM_FLAG_MEMBER.ALLOW_LEVER),
    ALLOW_BUTTON(false, CLAIM_FLAG_MEMBER.ALLOW_BUTTON),
    ALLOW_BED(false, CLAIM_FLAG_MEMBER.ALLOW_BED),
    ALLOW_REQUESTS(true),
    ALLOW_ITEM_PICKUP(false);

    final boolean defaultValue;
    private final CLAIM_FLAG_MEMBER memberEquivalent;

    CLAIM_FLAG(Boolean defaultValue) {
        this(defaultValue, null);
    }

    CLAIM_FLAG(boolean defaultValue, CLAIM_FLAG_MEMBER memberEquivalent) {
        this.defaultValue = defaultValue;
        this.memberEquivalent = memberEquivalent;
    }

    public Object getValue() {
        return HelperClaim.getHandler().getGlobalFlagDefaults().get(this);
    }
}
