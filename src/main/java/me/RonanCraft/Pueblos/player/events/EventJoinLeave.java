package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.Updater;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventJoinLeave {

    void leave(PlayerQuitEvent e) {
        Pueblos.getInstance().getPlayerData(e.getPlayer()).clear();
    }

    void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        updater(p);
    }

    //Recieve an update message when admin joins
    private void updater(Player p) {
        if (!FileOther.FILETYPE.CONFIG.getBoolean("DisableUpdater") && PermissionNodes.UPDATE.check(p))
            if (!getPl().getDescription().getVersion().equals(Updater.updatedVersion))
                Message.sms(p, "&7There is currently an update for &6Pueblos &7version &e#" +
                        Updater.updatedVersion + " &7you have version &e#" + getPl().getDescription().getVersion(), null);
    }

    private Pueblos getPl() {
        return Pueblos.getInstance();
    }
}
