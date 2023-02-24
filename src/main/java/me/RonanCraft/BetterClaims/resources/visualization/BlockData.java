/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.resources.visualization;

import lombok.Getter;
import org.bukkit.Material;

public class BlockData {

    @Getter Material mat;

    BlockData(Material mat) {
        this.mat = mat;
    }

}
