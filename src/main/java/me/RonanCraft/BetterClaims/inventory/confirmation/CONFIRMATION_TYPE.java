/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.inventory.confirmation;

public enum CONFIRMATION_TYPE {
    REQUEST_DECLINE, //Ignore request
    REQUEST_DECLINE_IGNORE, //Ignore any other incoming requests from this player
    CLAIM_LEAVE, //Leave a claim
    MEMBER_REMOVE, //Remove a member from a claim
    CLAIM_DELETE, //Delete a claim permanently
}
