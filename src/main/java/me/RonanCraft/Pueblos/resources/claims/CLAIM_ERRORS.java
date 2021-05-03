package me.RonanCraft.Pueblos.resources.claims;

import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum CLAIM_ERRORS {
    NONE(null),
    OVERLAPPING(MessagesCore.CLAIM_CREATE_FAILED_OTHERCLAIM), //Another claim is overlapping this area
    SIZE_SMALL(MessagesCore.CLAIM_CREATE_FAILED_SIZE), //Claim is too small
    SIZE_LARGE(MessagesCore.CLAIM_CREATE_FAILED_SIZELARGE), //Claim is too large
    DATABASE_ERROR(MessagesCore.CLAIM_CREATE_FAILED_DATABASE), //There was an error saving this claims information
    LOCATION_ALREADY_EXISTS(null), //Location was the same as the prior, making just a block protection instead of box
    CANCELLED(null), //The event error cancelled this action
    AUCTION_EXISTS(null), //An auction already exists for this claim
    ;

    private final MessagesCore msg;

    CLAIM_ERRORS(MessagesCore msg) {
        this.msg = msg;
    }

    public void sendMsg(CommandSender sendi, Object info) {
        if (this.msg != null)
            this.msg.send(sendi, info);
    }

    public String getMsg(Object info) {
        return msg.get(info);
    }
}
