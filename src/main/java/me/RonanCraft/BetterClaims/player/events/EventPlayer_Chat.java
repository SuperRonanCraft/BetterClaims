package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.tools.ChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EventPlayer_Chat implements Listener {

    private final ChatEvent chat;

    EventPlayer_Chat(ChatEvent chat) {
        this.chat = chat;
    }

    public void listen() {
        Bukkit.getPluginManager().registerEvents(this, BetterClaims.getInstance());
    }

    private void stop() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!e.getPlayer().equals(chat.player))
            return;
        e.getPlayer().sendMessage("Chat event!");
        stop();
    }
}
