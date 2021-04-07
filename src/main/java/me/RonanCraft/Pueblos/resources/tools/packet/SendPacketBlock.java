package me.RonanCraft.Pueblos.resources.tools.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.RonanCraft.Pueblos.Pueblos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SendPacketBlock {

    public SendPacketBlock(HashMap<Block, Material> blocks, Player p) {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();

            for (Map.Entry<Block, Material> blockEntry : blocks.entrySet()) {
                Block block = blockEntry.getKey();
                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE));
                packet.setBlockData(WrappedBlockData.createData(blockEntry.getValue()));
                packet.setLocation(new BlockPosition(block.getLocation().toVector()));
                packet.sendPacket(p);
            }
            remove(blocks, p);
        }
    }

    private void remove(HashMap<Block, Material> blocks, Player p) {
        Bukkit.getScheduler().runTaskLater(Pueblos.getInstance(), () -> {
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();

            for (Map.Entry<Block, Material> blockEntry : blocks.entrySet()) {
                Block block = blockEntry.getKey();
                WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE));
                packet.setBlockData(WrappedBlockData.createData(block.getType()));
                packet.setLocation(new BlockPosition(block.getLocation().toVector()));
                packet.sendPacket(p);
            }
        }, 20 * 10);
    }
}
