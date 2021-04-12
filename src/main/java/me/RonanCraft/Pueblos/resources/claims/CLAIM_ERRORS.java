package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.claims.events.ClaimEvent_Create;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum CLAIM_ERRORS {
    NONE(null),
    OVERLAPPING(MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM),
    SIZE(MessagesCore.CLAIM_CREATE_FAILED_SIZE),
    DATABASE_ERROR(MessagesCore.CLAIM_CREATE_FAILED_DATABASE),
    LOCATION_ALREADY_EXISTS(null);

    private final MessagesCore msg;

    CLAIM_ERRORS(MessagesCore msg) {
        this.msg = msg;
    }

    public void sendMsg(Player p, Object info) {
        if (this.msg != null)
            this.msg.send(p, info);
    }
}
