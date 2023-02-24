package me.RonanCraft.BetterClaims.resources.tools;

import org.bukkit.entity.Player;

public class ChatEvent {

    public final Player player;
    final CHAT_EVENT_RESULT chat_event_result;

    public ChatEvent(Player player, CHAT_EVENT_RESULT chat_event_result) {
        this.player = player;
        this.chat_event_result = chat_event_result;
    }

    public enum CHAT_EVENT_RESULT {
        CLAIM_RENAME
    }
}
