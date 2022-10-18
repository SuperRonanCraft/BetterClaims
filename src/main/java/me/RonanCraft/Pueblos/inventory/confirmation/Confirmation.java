/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.Pueblos.inventory.confirmation;

import org.bukkit.entity.Player;

public class Confirmation {

    public final CONFIRMATION_TYPE type;
    public final Player p;
    public final Object info;

    public Confirmation(CONFIRMATION_TYPE type, Player p, Object info) {
        this.type = type;
        this.p = p;
        this.info = info;
    }


}
