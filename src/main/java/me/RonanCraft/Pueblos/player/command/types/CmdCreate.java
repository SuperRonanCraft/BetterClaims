package me.RonanCraft.Pueblos.player.command.types;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import me.RonanCraft.Pueblos.player.command.PueblosCommandHelpable;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
import me.RonanCraft.Pueblos.resources.claims.ClaimEvents;
import me.RonanCraft.Pueblos.resources.claims.ClaimHandler;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import me.RonanCraft.Pueblos.resources.tools.packet.SendPacketBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdCreate implements PueblosCommand, PueblosCommandHelpable {

    public String getName() {
        return "create";
    }

    public void execute(CommandSender sendi, String label, String[] args) {
        Player p = (Player) sendi;
        ClaimHandler handler = Pueblos.getInstance().getSystems().getClaimHandler();

        //Event
        Claim claim = handler.claimCreate(p.getUniqueId(), p.getName(), new ClaimPosition(p.getLocation().getWorld(),
                p.getLocation().clone().add(-8, 0, -8),
                p.getLocation().clone().add(8, 0, 8)));
        if (claim != null) {
            ClaimEvents.create(claim);
            Pueblos.getInstance().getSystems().getDatabase().createClaim(claim);
            Messages.core.sms(sendi, "&aClaim was created!");
            showCorners(p, claim);
        } else {
            Messages.core.sms(sendi, "&cClaim was NOT created!");
        }


    }

    public static void showCorners(Player p, Block block) {
        HashMap<Block, Material> blocks = new HashMap<>();
        blocks.put(block, Material.GOLD_BLOCK);
        new SendPacketBlock(blocks, p);
    }

    public static void showCorners(Player p, Claim claim) {
        ClaimPosition pos = claim.getPosition();
        List<Block> corners = new ArrayList<>();
        //Top Right
        corners.add(pos.getWorld().getHighestBlockAt(pos.getRight(), pos.getTop()));
        //Bottom Right
        corners.add(pos.getWorld().getHighestBlockAt(pos.getRight(), pos.getBottom()));
        //Top Left
        corners.add(pos.getWorld().getHighestBlockAt(pos.getLeft(), pos.getTop()));
        //Bottom Left
        corners.add(pos.getWorld().getHighestBlockAt(pos.getLeft(), pos.getBottom()));
        HashMap<Block, Material> blocks = new HashMap<>();
        for (Block block : corners) {
            blocks.put(block, Material.GOLD_BLOCK);
        }
        new SendPacketBlock(blocks, p);
    }

    public boolean permission(CommandSender sendi) {
        return true;
    }

    @Override
    public String getHelp() {
        return Messages.help.getHelpCreate();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
