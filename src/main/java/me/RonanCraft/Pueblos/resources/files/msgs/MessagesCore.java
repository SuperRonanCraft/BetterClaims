package me.RonanCraft.Pueblos.resources.files.msgs;

import org.bukkit.command.CommandSender;

public class MessagesCore implements Message {
    private static final String pre = "Messages.";

    public void sendReload(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Reload"));
    }

    public void sendNoPermission(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "NoPermission"));
    }

    public void sendInvalidCommand(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "InvalidCommand"));
    }

    public void sendClaimItemInClaim(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Claim.Item.InClaim"));
    }

    public void sendClaimItemNoClaim(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Claim.Item.NoClaim"));
    }

    public void sendClaimItemNotOwner(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Claim.Item.NotOwner"));
    }

    public void sendClaimCreateSuccess(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Claim.Create.Success"));
    }

    public void sendClaimCreateFailedSize(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Claim.Create.Failed.Size"));
    }

    public void sendClaimCreateFailedOtherClaim(CommandSender sendi) {
        sms(sendi, getLang().getString(pre + "Claim.Create.Failed.OtherClaim"));
    }
}
