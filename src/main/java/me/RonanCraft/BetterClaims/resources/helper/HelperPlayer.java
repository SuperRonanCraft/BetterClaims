/*
 * Copyright (c) 2022 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims.resources.helper;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.Claim;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.Claim_Child;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.claims.data.Claim_Request;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_MODE;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_TYPE;
import me.RonanCraft.BetterClaims.database.DatabaseClaims;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.player.events.PlayerClaimInteraction;
import me.RonanCraft.BetterClaims.resources.Settings;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.visualization.Visualization;
import me.RonanCraft.BetterClaims.resources.visualization.VisualizationType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HelperPlayer {

    public static PlayerData getData(Player p) {
        return BetterClaims.getInstance().getPlayerData(p);
    }

}
