package me.RonanCraft.Pueblos.database;

import me.RonanCraft.Pueblos.Pueblos;

import java.util.logging.Level;

public class Error {
    public static void execute(Pueblos plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Pueblos plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
