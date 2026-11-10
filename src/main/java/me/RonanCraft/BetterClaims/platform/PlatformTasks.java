package me.RonanCraft.BetterClaims.platform;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import java.util.concurrent.CompletableFuture;

/** Routes operations to their owning server context; never waits for another region. */
public final class PlatformTasks implements AutoCloseable {
    private final PlatformScheduler scheduler;
    private final boolean folia;
    private volatile boolean closed;
    private final Object stateLock = new Object();
    /** Serializes plugin data, without changing the caller's region ownership.
     * Never wait for a scheduled task or another region while holding this lock. */
    public Object stateLock() { return stateLock; }
    public void state(Runnable action) { synchronized (stateLock) { action.run(); } }
    private void scheduled(Runnable action) { synchronized (stateLock) { if (!closed) action.run(); } }
    private Runnable guarded(Runnable action) { return () -> scheduled(action); }
    public PlatformTasks(Plugin plugin) {
        FoliaLib library = new FoliaLib(plugin);
        scheduler = library.getScheduler();
        folia = library.isFolia();
    }
    PlatformTasks(PlatformScheduler scheduler, boolean folia) {
        this.scheduler = scheduler;
        this.folia = folia;
    }
    public boolean isFolia() { return folia; }
    public boolean owns(Location location) { return scheduler.isOwnedByCurrentRegion(location); }
    public boolean owns(Entity entity) { return scheduler.isOwnedByCurrentRegion(entity); }
    public void global(Runnable action) { if (closed) return; scheduler.runNextTick(task -> scheduled(action)); }
    public void async(Runnable action) { if (!closed) scheduler.runAsync(task -> { if (!closed) action.run(); }); }
    public void entity(Entity entity, Runnable action) {
        if (closed) return;
        scheduler.runAtEntity(entity, task -> scheduled(action));
    }
    public void sender(CommandSender sender, Runnable action) {
        if (sender instanceof Entity) entity((Entity) sender, action); else global(action);
    }
    public void player(java.util.UUID id, java.util.function.Consumer<CommandSender> action) {
        if (closed) return;
        if (id == null) { global(() -> action.accept(org.bukkit.Bukkit.getConsoleSender())); return; }
        org.bukkit.entity.Player player = scheduler.getPlayer(id);
        if (player == null) global(() -> action.accept(null));
        else scheduler.runAtEntityWithFallback(player, task -> scheduled(() -> action.accept(player)),
                () -> global(() -> action.accept(null)));
    }
    public void location(Location location, Runnable action) {
        if (closed) return;
        scheduler.runAtLocation(location.clone(), task -> scheduled(action));
    }
    public WrappedTask later(Entity entity, Runnable action, long ticks) {
        if (closed) return null;
        return scheduler.runAtEntityLater(entity, guarded(action), Math.max(1L, ticks));
    }
    public WrappedTask laterGlobal(Runnable action, long ticks) {
        if (closed) return null;
        return scheduler.runLater(guarded(action), Math.max(1L, ticks));
    }
    public WrappedTask laterSender(CommandSender sender, Runnable action, long ticks) {
        if (closed) return null;
        return sender instanceof Entity ? later((Entity) sender, action, ticks)
                : scheduler.runLater(guarded(action), Math.max(1L, ticks));
    }
    public WrappedTask repeat(Runnable action, long delay, long period) {
        if (closed) return null;
        return scheduler.runTimer(guarded(action), Math.max(1L, delay), Math.max(1L, period));
    }
    public WrappedTask repeatAsync(Runnable action, long delay, long period) {
        if (closed) return null;
        return scheduler.runTimerAsync(action, Math.max(1L, delay), Math.max(1L, period));
    }
    public CompletableFuture<Boolean> teleport(Entity entity, Location location) {
        if (closed) return CompletableFuture.completedFuture(false);
        return scheduler.teleportAsync(entity, location.clone());
    }
    @Override public void close() { closed = true; scheduler.cancelAllTasks(); }
}
