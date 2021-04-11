package me.RonanCraft.Pueblos.resources.files.msgs;

import org.bukkit.command.CommandSender;

public enum MessagesCore {
    RELOAD("Reload"),
    NOPERMISSION("NoPermission"),
    INVALIDCOMMAND("InvalidCommand"),
    INVALIDFLAGVALUE("InvalidFlagValue"),
    INVALIDFLAG("InvalidFlag"),
    CANNOTEDIT("CannotEdit"),
    CLAIM_NONE("Claim.None"),
    CLAIM_NOPERMISSION("Claim.NoPermission"),
    CLAIM_FLAGCHANGE("Claim.Flag"),
    CLAIM_ITEM_INCLAIM("Claim.Item.InClaim"),
    CLAIM_ITEM_NOCLAIM("Claim.Item.NoClaim"),
    CLAIM_ITEM_NOTOWNER("Claim.Item.NotOwner"),
    CLAIM_CREATE_SUCCESS("Claim.Create.Success"),
    CLAIM_CREATE_FAILED_SIZE("Claim.Create.Failed.Size"),
    CLAIM_CREATE_FAILED_OTHERCLAIM("Claim.Create.Failed.OtherClaim"),
    CLAIM_MEMBER_REMOVED("Claim.Member.Removed"),
    CLAIM_MEMBER_LEAVE("Claim.Member.Leave"),
    CLAIM_MEMBER_NOTIFICATION_REMOVED("Claim.Member.Notification.Removed"),
    CLAIM_MEMBER_NOTIFICATION_LEAVE("Claim.Member.Notification.Leave"),
    REQUEST_NEW("Request.New"),
    REQUEST_ACCEPTED("Request.Accepted"),
    REQUEST_DENIED("Request.Denied"),
    REQUEST_REQUESTER_ACCEPTED("Request.Requester.Accepted"),
    REQUEST_REQUESTER_DENIED("Request.Requester.Denied"),
    REQUEST_REQUESTER_ALREADY("Request.Requester.Already"),
    REQUEST_REQUESTER_SENT("Request.Requester.Sent"),
    ;

    String section;

    MessagesCore(String section) {
        this.section = section;
    }

    private static final String pre = "Messages.";

    public void send(CommandSender sendi) {
        Message.sms(sendi, Message.getLang().getString(pre + section), null);
    }

    public void send(CommandSender sendi, Object placeholderInfo) {
        Message.sms(sendi, Message.getLang().getString(pre + section), placeholderInfo);
    }
}
