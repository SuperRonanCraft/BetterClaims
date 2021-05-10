/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.resources.claims;

class ClaimUpdates {

    boolean updated = false;

    public void updated(boolean update) {
        if (update)
            updated = true;
    }

    public boolean wasUpdated() {
        return updated;
    }

    public void uploaded() {
        updated = false;
    }
}
