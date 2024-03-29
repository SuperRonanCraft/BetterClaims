package me.RonanCraft.BetterClaims.resources.messages;

import org.bukkit.command.CommandSender;

public enum MessagesUsage {

    CONVERT("Convert"),
    AUCTION_CREATE("Auction.Create"),
    CLAIM_ADDMEMBER("AddMember"),
    ;

    String section;

    MessagesUsage(String section) {
        this.section = section;
    }

    private static final String pre = "Usage.";

    public void send(CommandSender sendi) {
        Message.sms(sendi, Message.getLang().getString(pre + section), null);
    }

    public void send(CommandSender sendi, Object placeholderInfo) {
        Message.sms(sendi, Message.getLang().getString(pre + section), placeholderInfo);
    }

    public String get(Object placeholderInfo) {
        return Message.placeholder(null, Message.getLang().getString(pre + section), placeholderInfo);
    }

}
