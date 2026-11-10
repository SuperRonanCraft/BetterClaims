package me.RonanCraft.BetterClaims.platform;

import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static org.junit.Assert.*;

public class PlatformTasksTest {
    private String method;
    private Object[] args;
    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (p,m,a) -> null);
    }
    private PlatformTasks tasks(Player online) {
        PlatformScheduler scheduler = (PlatformScheduler) Proxy.newProxyInstance(
            PlatformScheduler.class.getClassLoader(), new Class<?>[]{PlatformScheduler.class}, (p,m,a) -> {
                method = m.getName(); args = a;
                if (method.equals("getPlayer")) return online;
                if (m.getReturnType() == CompletableFuture.class) return CompletableFuture.completedFuture(null);
                return null;
            });
        return new PlatformTasks(scheduler, true);
    }
    @Test public void routesPlayersAndConsoleToDifferentOwners() {
        PlatformTasks tasks = tasks(null);
        Player player = proxy(Player.class);
        tasks.sender(player, () -> {});
        assertEquals("runAtEntity", method); assertSame(player, args[0]);
        tasks.sender(proxy(CommandSender.class), () -> {});
        assertEquals("runNextTick", method);
    }
    @Test @SuppressWarnings("unchecked") public void editorResponseFollowsPlayerAndRetirementHasAFallback() {
        Player player = proxy(Player.class);
        PlatformTasks tasks = tasks(player);
        AtomicInteger calls = new AtomicInteger();
        tasks.player(UUID.randomUUID(), recipient -> { assertNull(recipient); calls.incrementAndGet(); });
        assertEquals("runAtEntityWithFallback", method); assertSame(player, args[0]);
        assertEquals(0, calls.get());
        Runnable retired = (Runnable) args[2];
        retired.run();
        assertEquals("runNextTick", method);
        ((Consumer<WrappedTask>) args[0]).accept(null);
        assertEquals(1, calls.get());
    }
    @Test public void deferredTeleportCopiesDestinationAndShutdownCancelsTasks() {
        PlatformTasks tasks = tasks(null);
        Location destination = new Location(null, 1, 2, 3);
        tasks.teleport(proxy(Player.class), destination);
        assertEquals("teleportAsync", method);
        Location submitted = (Location) args[1];
        destination.setX(99);
        assertEquals(1, submitted.getX(), 0);
        tasks.close(); assertEquals("cancelAllTasks", method);
    }
    @Test public void entityDelayClampsZeroToOneTick() {
        PlatformTasks tasks = tasks(null);
        Player player = proxy(Player.class);
        tasks.later(player, () -> {}, 0);
        assertEquals("runAtEntityLater", method); assertSame(player,args[0]); assertEquals(1L,args[2]);
    }
    @Test @SuppressWarnings("unchecked") public void editorReceivesTheScheduledPlayerInstance() {
        Player player = proxy(Player.class);
        PlatformTasks tasks = tasks(player);
        AtomicInteger calls = new AtomicInteger();
        tasks.player(UUID.randomUUID(), recipient -> { assertSame(player, recipient); calls.incrementAndGet(); });
        ((Consumer<WrappedTask>) args[1]).accept(null);
        assertEquals(1, calls.get());
    }
    @Test @SuppressWarnings("unchecked") public void offlineEditorCallbackReceivesNull() {
        PlatformTasks tasks = tasks(null);
        AtomicInteger calls = new AtomicInteger();
        tasks.player(UUID.randomUUID(), recipient -> { assertNull(recipient); calls.incrementAndGet(); });
        assertEquals("runNextTick", method);
        ((Consumer<WrappedTask>) args[0]).accept(null);
        assertEquals(1, calls.get());
    }
    @Test @SuppressWarnings("unchecked") public void shutdownSuppressesAlreadyQueuedCallbacks() {
        PlatformTasks tasks = tasks(null);
        AtomicInteger calls = new AtomicInteger();
        tasks.entity(proxy(Player.class), calls::incrementAndGet);
        Consumer<WrappedTask> queued = (Consumer<WrappedTask>) args[1];
        tasks.close();
        queued.accept(null);
        tasks.global(calls::incrementAndGet);
        assertEquals("cancelAllTasks", method);
        assertEquals(0, calls.get());
    }
    @Test public void locationSchedulingCopiesCoordinates() {
        PlatformTasks tasks = tasks(null);
        Location location = new Location(null, 4, 5, 6);
        tasks.location(location, () -> {});
        Location scheduled = (Location) args[0];
        location.setX(100);
        assertEquals(4, scheduled.getX(), 0);
    }
    @Test public void concurrentPluginTransactionsCannotLoseUpdates() throws Exception {
        PlatformTasks tasks = tasks(null);
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(4);
        int[] count = {0};
        java.util.List<java.util.concurrent.Future<?>> jobs = new java.util.ArrayList<>();
        try {
            for (int i = 0; i < 4; i++) jobs.add(pool.submit(() -> {
                for (int j = 0; j < 1000; j++) tasks.state(() -> {
                    int observed = count[0];
                    Thread.yield();
                    count[0] = observed + 1;
                });
            }));
            for (java.util.concurrent.Future<?> job : jobs) job.get(10, java.util.concurrent.TimeUnit.SECONDS);
            assertEquals(4000, count[0]);
        } finally { pool.shutdownNow(); }
    }
}
