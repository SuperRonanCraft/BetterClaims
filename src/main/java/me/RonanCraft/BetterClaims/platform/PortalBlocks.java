package me.RonanCraft.BetterClaims.platform;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.PortalCreateEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** The portal API changed both return type and element type after 1.8. */
public final class PortalBlocks {
    private static final Method BLOCKS;
    static {
        try { BLOCKS = PortalCreateEvent.class.getMethod("getBlocks"); }
        catch (NoSuchMethodException error) { throw new ExceptionInInitializerError(error); }
    }
    private PortalBlocks() { }
    public static List<Location> locations(PortalCreateEvent event) {
        try {
            List<Location> result = new ArrayList<>();
            for (Object block : (Iterable<?>) BLOCKS.invoke(event)) {
                if (block instanceof Block) result.add(((Block) block).getLocation());
                else if (block instanceof BlockState) result.add(((BlockState) block).getLocation());
                else throw new IllegalStateException("Unknown portal block type: " + block.getClass());
            }
            return result;
        } catch (ReflectiveOperationException error) { throw new IllegalStateException("Cannot inspect portal blocks", error); }
    }
}
