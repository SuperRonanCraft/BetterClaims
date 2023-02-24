package me.RonanCraft.BetterClaims.database;

import me.RonanCraft.BetterClaims.BetterClaims;

import java.util.logging.Level;

public class Error {
    public static void execute(BetterClaims plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(BetterClaims plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
