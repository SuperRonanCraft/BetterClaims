package me.RonanCraft.BetterClaims.resources.messages;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.Settings;
import me.RonanCraft.BetterClaims.claims.*;
import me.RonanCraft.BetterClaims.claims.data.Claim_Request;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_FLAG_MEMBER;
import me.RonanCraft.BetterClaims.claims.data.members.Member;
import me.RonanCraft.BetterClaims.auction.Auction;
import me.RonanCraft.BetterClaims.resources.files.FileLanguage;
import me.RonanCraft.BetterClaims.inventory.confirmation.Confirmation;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.helper.HelperDate;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {
    static FileLanguage getLang() {
        return BetterClaims.getInstance().getFiles().getLang();
    }

    public static void sms(CommandSender sendi, String msg, Object placeholderInfo) {
        if (!msg.equals(""))
            sendi.sendMessage(placeholder(sendi, getPrefix() + msg, placeholderInfo));
    }

    public static void sms(CommandSender sendi, List<String> msg, Object placeholderInfo) {
        if (msg != null && !msg.isEmpty()) {
            msg.forEach(str -> msg.set(msg.indexOf(str), placeholder(sendi, str, placeholderInfo)));
            sendi.sendMessage(msg.toArray(new String[0]));
        }
    }

    private static String getPrefix() {
        return getLang().getString("Messages.Prefix");
    }

    public static String placeholder(CommandSender p, String str, Object info) {
        if (str != null) {
            if (BetterClaims.getInstance().papiEnabled())
                try {
                    str = PlaceholderAPI.setPlaceholders((Player) p, str);
                } catch (Exception e) {
                    //Something went wrong with PAPI
                }
            if (str.contains("%player_name%"))
                str = str.replaceAll("%player_name%", p.getName());
            if (str.contains("%player_uuid%"))
                if (p instanceof Player)
                    str = str.replaceAll("%player_uuid%", ((Player) p).getUniqueId().toString());
            //Placeholders based off info
            if (info instanceof String) {
                if (str.contains("%command%"))
                    str = str.replace("%command%", (String) info);
                if (str.contains("%invalid_name%"))
                    str = str.replace("%invalid_name%", (String) info);
            }
            else if (info instanceof ClaimData)
                str = claims(str, (ClaimData) info, null);
            else if (info instanceof Member)
                str = member(str, (Member) info, null);
            else if (info instanceof Claim_Request)
                str = requests(str, (Claim_Request) info);
            else if (info instanceof Confirmation)
                str = confirmation(str, (Confirmation) info);
            else if (info instanceof Auction)
                str = auctions(str, (Auction) info);
            else if (info instanceof Object[] && ((Object[]) info).length == 2)
                str = getPlaceholder(str, (Object[]) info);
        }
        if (str != null)
            return ChatColor.translateAlternateColorCodes('&', str);
        return null;
    }

    //Multiple variables
    private static String getPlaceholder(String str, Object[] info) {
        if (info[0] instanceof Member && info[1] instanceof CLAIM_FLAG_MEMBER)
            str = member(str, (Member) info[0], (CLAIM_FLAG_MEMBER) info[1]);
        else if (info[0] instanceof ClaimData && info[1] instanceof CLAIM_FLAG)
            str = claims(str, (ClaimData) info[0], (CLAIM_FLAG) info[1]);
        return str;
    }

    //Claims
    private static String claims(String str, ClaimData claimData, CLAIM_FLAG flag) {
        if (str.contains("%claim_name%"))
            str = str.replace("%claim_name%", claimData.getClaimName());
        if (str.contains("%claim_location_world%"))
            str = str.replace("%claim_location_world%", claimData.getWorld().getName());
        if (str.contains("%claim_location%"))
            str = str.replace("%claim_location%", HelperClaim.getLocationString(claimData));
        if (str.contains("%claim_members%"))
            str = str.replace("%claim_members%", String.valueOf(claimData.getMembers().size()));
        if (str.contains("%claim_owner%"))
            str = str.replace("%claim_owner%", String.valueOf(claimData.getOwnerName()));
        if (str.contains("%claim_requests%"))
            str = str.replace("%claim_requests%", String.valueOf(claimData.getRequests().size()));
        if (str.contains("%claim_width%"))
            str = str.replace("%claim_width%", String.valueOf(claimData.getBoundingBox().getWidth()));
        if (str.contains("%claim_length%"))
            str = str.replace("%claim_length%", String.valueOf(claimData.getBoundingBox().getLength()));
        if (str.contains("%claim_id%"))
            str = str.replace("%claim_id%", String.valueOf(claimData.claimId));
        if (flag != null) {
            if (str.contains("%claim_flag%"))
                str = str.replace("%claim_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%claim_flag_value%"))
                str = str.replace("%claim_flag_value%", claimData.getFlags().getFlag(flag).toString());
        }
        if (str.contains("%max%"))
            str = str.replace("%max%", String.valueOf(BetterClaims.getInstance().getSettings().getInt(Settings.SETTING.CLAIM_MAXSIZE)));
        return str;
    }

    //Claim Members
    private static String member(String str, Member member, CLAIM_FLAG_MEMBER flag) {
        if (str.contains("%member_name%"))
            str = str.replace("%member_name%", member.getName());
        if (str.contains("%member_date%"))
            str = str.replace("%member_date%", HelperDate.getDate(member.date));
        if (flag != null) {
            if (str.contains("%member_flag%"))
                str = str.replace("%member_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%member_flag_value%"))
                str = str.replace("%member_flag_value%", member.getFlags().getOrDefault(flag, flag.getDefault()).toString());
        }
        return claims(str, member.claimData, null);
    }

    //Claim Requests
    private static String requests(String str, Claim_Request request) {
        if (str.contains("%request_name%"))
            str = str.replace("%request_name%", request.name);
        if (str.contains("%request_date%"))
            str = str.replace("%request_date%", HelperDate.getDate(request.date));
        return claims(str, request.claimData, null);
    }

    //Confirmations
    private static String confirmation(String str, Confirmation confirmation) {
        if (str.contains("%confirm_type%"))
            str = str.replace("%confirm_type%", StringUtils.capitalize(confirmation.type.name().toLowerCase().replace("_", " ")));
        if (str.contains("%confirm_action%")) {
            String action = "";
            if (confirmation.info instanceof Member)
                action = ((Member) confirmation.info).name;
            str = str.replace("%confirm_action%", action);
        }
        return str;
    }

    //Auctions
    private static String auctions(String str, Auction auction) {
        if (str.contains("%auction_price%"))
            str = str.replace("%auction_price%", String.valueOf(auction.getPrice()));
        if (str.contains("%auction_time%"))
            str = str.replace("%auction_time%", String.valueOf(auction.getPrice()));
        return claims(str, auction.claimData, null);
    }
}
