package me.RonanCraft.BetterClaims.resources.visualization;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

/** A detached packet payload; no world access is needed when sending it to a player. */
public class BlockData {
    @Getter final Material mat;
    private final byte legacyData;
    private final Object modernData;
    private static final Method READ, CREATE, SEND;
    static {
        Method read = null, create = null, send = null;
        try {
            Class<?> data = Class.forName("org.bukkit.block.data.BlockData");
            read = Block.class.getMethod("getBlockData");
            create = Material.class.getMethod("createBlockData");
            send = Player.class.getMethod("sendBlockChange", Location.class, data);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            // Spigot 1.8–1.12 uses material + byte data instead.
        }
        READ = read; CREATE = create; SEND = send;
    }
    BlockData(Material mat) {
        this.mat = mat;
        legacyData = 0;
        modernData = invoke(CREATE, mat);
    }
    BlockData(Block block) {
        mat = block.getType();
        legacyData = READ == null ? block.getData() : 0;
        modernData = invoke(READ, block);
    }
    void send(Player player, Location location) {
        if (SEND == null) player.sendBlockChange(location, mat, legacyData);
        else invoke(SEND, player, location, modernData);
    }
    private static Object invoke(Method method, Object receiver, Object... args) {
        if (method == null) return null;
        try { return method.invoke(receiver, args); }
        catch (ReflectiveOperationException error) { throw new IllegalStateException("Unable to access block data", error); }
    }
}
