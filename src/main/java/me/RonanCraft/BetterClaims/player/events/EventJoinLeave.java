package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.Updater;
import me.RonanCraft.BetterClaims.resources.PermissionNodes;
import me.RonanCraft.BetterClaims.resources.files.FileOther;
import me.RonanCraft.BetterClaims.resources.messages.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventJoinLeave {

    void leave(PlayerQuitEvent e) {
        BetterClaims.getInstance().getPlayerData(e.getPlayer()).clear();
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

    private BetterClaims getPl() {
        return BetterClaims.getInstance();
    }
}
