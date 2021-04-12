package me.RonanCraft.Pueblos.resources.files.msgs;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.FileLanguage;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {
    static FileLanguage getLang() {
        return Pueblos.getInstance().getFiles().getLang();
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
            if (Pueblos.getInstance().PlaceholderAPI)
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
            if (info instanceof Claim)
                str = claims(str, (Claim) info, null);
            else if (info instanceof ClaimMember)
                str = member(str, (ClaimMember) info, null);
            else if (info instanceof ClaimRequest)
                str = requests(str, (ClaimRequest) info);
            else if (info instanceof Confirmation)
                str = confirmation(str, (Confirmation) info);
            else if (info instanceof Object[] && ((Object[]) info).length == 2)
                str = getPlaceholder(str, (Object[]) info);
        }
        if (str != null)
            return ChatColor.translateAlternateColorCodes('&', str);
        return null;
    }

    //Multiple variables
    private static String getPlaceholder(String str, Object[] info) {
        if (info[0] instanceof ClaimMember && info[1] instanceof CLAIM_FLAG_MEMBER)
            str = member(str, (ClaimMember) info[0], (CLAIM_FLAG_MEMBER) info[1]);
        else if (info[0] instanceof Claim && info[1] instanceof CLAIM_FLAG)
            str = claims(str, (Claim) info[0], (CLAIM_FLAG) info[1]);
        return str;
    }

    //Claims
    private static String claims(String str, Claim claim, CLAIM_FLAG flag) {
        if (str.contains("%claim_name%"))
            str = str.replace("%claim_name%", claim.getName());
        if (str.contains("%claim_location_world%"))
            str = str.replace("%claim_location_world%", claim.getPosition().getWorld().getName());
        if (str.contains("%claim_location%"))
            str = str.replace("%claim_location%", HelperClaim.getLocationString(claim));
        if (str.contains("%claim_members%"))
            str = str.replace("%claim_members%", String.valueOf(claim.getMembers().size()));
        if (str.contains("%claim_owner%"))
            str = str.replace("%claim_owner%", String.valueOf(claim.ownerName));
        if (str.contains("%claim_requests%"))
            str = str.replace("%claim_requests%", String.valueOf(claim.getRequests().size()));
        if (str.contains("%claim_requests%"))
            str = str.replace("%claim_requests%", String.valueOf(claim.getRequests().size()));
        if (str.contains("%claim_width%"))
            str = str.replace("%claim_width%", String.valueOf(claim.getPosition().getGreaterBoundaryCorner().getBlockX()
                    - claim.getPosition().getLesserBoundaryCorner().getBlockX()));
        if (str.contains("%claim_length%"))
            str = str.replace("%claim_length%", String.valueOf(claim.getPosition().getGreaterBoundaryCorner().getBlockZ()
                    - claim.getPosition().getLesserBoundaryCorner().getBlockZ()));
        if (flag != null) {
            if (str.contains("%claim_flag%"))
                str = str.replace("%claim_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%claim_flag_value%"))
                str = str.replace("%claim_flag_value%", claim.getFlags().getFlag(flag).toString());
        }
        return str;
    }

    //Claim Members
    private static String member(String str, ClaimMember member, CLAIM_FLAG_MEMBER flag) {
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
        return claims(str, member.claim, null);
    }

    //Claim Requests
    private static String requests(String str, ClaimRequest request) {
        if (str.contains("%request_name%"))
            str = str.replace("%request_name%", request.name);
        if (str.contains("%request_date%"))
            str = str.replace("%request_date%", HelperDate.getDate(request.date));
        return claims(str, request.claim, null);
    }

    //Confirmations
    private static String confirmation(String str, Confirmation confirmation) {
        if (str.contains("%confirm_type%"))
            str = str.replace("%confirm_type%", StringUtils.capitalize(confirmation.type.name().toLowerCase().replace("_", " ")));
        if (str.contains("%confirm_action%")) {
            String action = "";
            if (confirmation.info instanceof ClaimMember)
                action = ((ClaimMember) confirmation.info).name;
            str = str.replace("%confirm_action%", action);
        }
        return str;
    }
}
