package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG_MEMBER;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class PueblosPlaceholders {

    public static String getPlaceholder(String str, Player p, Object info) {
        if (info == null)
            return str;
        if (info instanceof Claim)
            str = getPlaceholder(str, (Claim) info);
        else if (info instanceof ClaimMember)
            str = getPlaceholder(str, (ClaimMember) info, null);
        else if (info instanceof Object[] && ((Object[]) info).length == 2)
            str = getPlaceholder(str, (Object[]) info);
        return Messages.core.color(p, str);
    }

    private static String getPlaceholder(String str, Object[] info) {
        if (info[0] instanceof ClaimMember && info[1] instanceof CLAIM_FLAG_MEMBER)
            str = getPlaceholder(str, (ClaimMember) info[0], (CLAIM_FLAG_MEMBER) info[1]);
        return str;
    }

    private static String getPlaceholder(String str, Claim info) {
        if (str.contains("%claim_name%"))
            str = str.replace("%claim_name%", info.getName());
        if (str.contains("%claim_members%"))
            str = str.replace("%claim_members%", String.valueOf(info.getMembers().size()));
        if (str.contains("%claim_owner%"))
            str = str.replace("%claim_owner%", String.valueOf(info.ownerName));
        return str;
    }

    private static String getPlaceholder(String str, ClaimMember info, CLAIM_FLAG_MEMBER flag) {
        if (str.contains("%member_name%"))
            str = str.replace("%member_name%", info.getName());
        if (flag != null) {
            if (str.contains("%member_flag%"))
                str = str.replace("%member_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%member_flag_value%"))
                str = str.replace("%member_flag_value%", info.getFlags().getOrDefault(flag, flag.getDefault()).toString());
        }
        return str;
    }

}
