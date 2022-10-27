package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.player.command.PueblosCommandTabComplete;
import me.RonanCraft.Pueblos.resources.PermissionNodes;
import me.RonanCraft.Pueblos.claims.ClaimData;
import me.RonanCraft.Pueblos.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.messages.MessagesCore;
import me.RonanCraft.Pueblos.resources.messages.MessagesHelp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdList implements PueblosCommand, PueblosCommandHelpable, PueblosCommandTabComplete {

    public String getName() {
        return "list";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        ClaimHandler handler = Pueblos.getInstance().getClaimHandler();
        Player p = (Player) sendi;
        boolean admin = args.length > 1 && args[1].equalsIgnoreCase("admin") && PermissionNodes.ADMIN_CLAIM.check(sendi);
        List<ClaimData> claimData = handler.getClaims(p.getUniqueId(), admin);
        if (!claimData.isEmpty()) {
            if (claimData.size() == 1)
                PueblosInventory.CLAIM.open(p, claimData.get(0), true);
            else
                PueblosInventory.CLAIM_SELECT.open(p, claimData, true);
        } else
            MessagesCore.CLAIM_NONE.send(sendi);
    }

    public boolean permission(CommandSender sendi) {
        return PermissionNodes.USE.check(sendi);
    }

    @Override
    public String getHelp() {
        return MessagesHelp.LIST.get();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sendi, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2 && ("admin").startsWith(args[1]) && PermissionNodes.ADMIN_CLAIM.check(sendi))
            list.add("admin");
        return list;
    }
}
