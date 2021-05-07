package me.RonanCraft.Pueblos.resources.files.msgs;

import org.bukkit.command.CommandSender;

public enum MessagesCore {
    RELOAD("Reload"),
    NOPERMISSION("NoPermission"),
    INVALIDCOMMAND("InvalidCommand"),
    INVALIDFLAGVALUE("InvalidFlagValue"),
    INVALIDFLAG("InvalidFlag"),
    CANNOTEDIT("CannotEdit"),
    CLAIM_OVERRIDE_ENABLED("Claim.Override.Enabled"),
    CLAIM_OVERRIDE_DISABLED("Claim.Override.Disabled"),
    CLAIM_NONE("Claim.None"),
    CLAIM_TELEPORT("Claim.Teleport"),
    CLAIM_PERMISSION_CLAIM("Claim.Permission.Claim"),
    CLAIM_PERMISSION_ADMINCLAIM("Claim.Permission.AdminClaim"),
    CLAIM_FLAGCHANGE("Claim.Flag"),
    CLAIM_ITEM_INCLAIM("Claim.Item.InClaim"),
    CLAIM_ITEM_NOCLAIM("Claim.Item.NoClaim"),
    CLAIM_ITEM_NOTOWNER("Claim.Item.NotOwner"),
    CLAIM_ITEM_WAND_NORMAL("Claim.Item.Wand.Normal"),
    CLAIM_ITEM_WAND_SUBCLAIM("Claim.Item.Wand.SubClaim"),
    CLAIM_ITEM_WAND_ADMIN("Claim.Item.Wand.Admin"),
    CLAIM_ITEM_WAND_EDIT("Claim.Item.Wand.Edit"),
    CLAIM_CREATE_SUCCESS("Claim.Create.Success"),
    CLAIM_CREATE_FAILED_SIZE("Claim.Create.Failed.Size"),
    CLAIM_CREATE_FAILED_SIZELARGE("Claim.Create.Failed.SizeLarge"),
    CLAIM_CREATE_FAILED_OTHERCLAIM("Claim.Create.Failed.OtherClaim"),
    CLAIM_CREATE_FAILED_DATABASE("Claim.Create.Failed.Database"),
    CLAIM_MEMBER_REMOVED("Claim.Member.Removed"),
    CLAIM_MEMBER_LEAVE("Claim.Member.Leave"),
    CLAIM_MEMBER_NOTIFICATION_REMOVED("Claim.Member.Notification.Removed"),
    CLAIM_MEMBER_NOTIFICATION_LEAVE("Claim.Member.Notification.Leave"),
    CLAIM_RESIZE_SUCCESS("Claim.Resize.Success"),
    CLAIM_RESIZE_OVERLAPPING_CHILD("Claim.Resize.OverlappingChild"),
    CLAIM_RESIZE_OVERLAPPING_PARENT("Claim.Resize.OverlappingParent"),
    CLAIM_MODE_ENABLED_ADMIN("Claim.Mode.Enabled.Admin"),
    CLAIM_MODE_DISABLED_ADMIN("Claim.Mode.Disabled.Admin"),
    CLAIM_MODE_FAILED_ITEM("Claim.Mode.Failed.Item"),
    CLAIM_MODE_FAILED_LOCATION("Claim.Mode.Failed.Location"),
    CLAIM_UNKNOWNID("Claim.UnknownID"),
    CLAIM_DELETE("Claim.Deleted"),
    AUCTION_NOTINSIDE("Claim.Auction.NotInside"),
    AUCTION_NOTOWNER("Claim.Auction.NotOwner"),
    AUCTION_CREATED("Claim.Auction.Created"),
    AUCTION_DELETED("Claim.Auction.Deleted"),
    CONVERT_STARTED("Convert.Started"),
    CONVERT_FINISHED("Convert.Finished"),
    CONVERT_FAILED("Convert.Failed"),
    CONVERT_SUCCESS("Convert.Success"),
    CONVERT_UNKNOWN("Convert.Unknown"),
    REQUEST_NEW("Request.New"),
    REQUEST_ACCEPTED("Request.Accepted"),
    REQUEST_DENIED("Request.Denied"),
    REQUEST_REQUESTER_ACCEPTED("Request.Requester.Accepted"),
    REQUEST_REQUESTER_DENIED("Request.Requester.Denied"),
    REQUEST_REQUESTER_ALREADY("Request.Requester.Already"),
    REQUEST_REQUESTER_SENT("Request.Requester.Sent"),
    EVENT_DENIED("EventDenied"),
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

    public String get(Object placeholderInfo) {
        return Message.placeholder(null, Message.getLang().getString(pre + section), placeholderInfo);
    }
}
