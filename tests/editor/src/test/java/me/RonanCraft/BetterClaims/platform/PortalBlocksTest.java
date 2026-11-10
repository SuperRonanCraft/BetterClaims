package me.RonanCraft.BetterClaims.platform;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.world.PortalCreateEvent;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import static org.junit.Assert.*;

public class PortalBlocksTest {
    @Test public void readsLegacyPortalBlocksWithoutChangingTheEvent() {
        Location location = new Location(null, 7, 64, 9);
        Block block = (Block) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Block.class},
                (proxy, method, args) -> method.getName().equals("getLocation") ? location : null);
        PortalCreateEvent event = new PortalCreateEvent(Arrays.asList(block), null,
                PortalCreateEvent.CreateReason.OBC_DESTINATION);
        assertEquals(Arrays.asList(location), PortalBlocks.locations(event));
        assertFalse(event.isCancelled());
        assertSame(block, event.getBlocks().get(0));
    }
}
