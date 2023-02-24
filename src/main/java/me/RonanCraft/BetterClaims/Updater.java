package me.RonanCraft.BetterClaims;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Updater {
    public static String updatedVersion = BetterClaims.getInstance().getDescription().getVersion();

    public Updater(BetterClaims pl) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            try {
                URLConnection con = new URL(getUrl() + project()).openConnection();
                updatedVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            } catch (Exception ex) {
                Bukkit.getConsoleSender().sendMessage("[Pueblos] Failed to check for an update on spigot");
                updatedVersion = pl.getDescription().getVersion();
            }
        });
    }

    private String getUrl() {
        return "https://api.spigotmc.org/legacy/update.php?resource=";
    }

    private String project() {
        return "91255";
    }
}
