package me.RonanCraft.BetterClaims.platform;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Test;
import java.io.File;
import java.lang.reflect.Proxy;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

/** Loads the relocated library from the distributable, using only the 1.8.8 API. */
public class PackagedSchedulerTest {
    @Test public void packagedSchedulerLoadsAndRunsWithoutModernServerClasses() throws Exception {
        File jar = new File("../../target/BetterClaims-3.0.4.jar");
        org.junit.Assume.assumeTrue("Run mvn package in the project root first", jar.isFile());
        java.util.List<Runnable> pending = new java.util.ArrayList<>();
        BukkitTask task = (BukkitTask) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{BukkitTask.class},
                (proxy, method, args) -> method.getReturnType() == int.class ? 1 : null);
        BukkitScheduler scheduler = (BukkitScheduler) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{BukkitScheduler.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("runTask")) { pending.add((Runnable) args[1]); return task; }
                    return null;
                });
        Server server = (Server) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Server.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getScheduler")) return scheduler;
                    if (method.getName().equals("isPrimaryThread")) return true;
                    return null;
                });
        Plugin plugin = (Plugin) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Plugin.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getServer")) return server;
                    if (method.getName().equals("getLogger")) return java.util.logging.Logger.getLogger("packaged-test");
                    return null;
                });
        try (URLClassLoader loader = new URLClassLoader(new java.net.URL[]{jar.toURI().toURL()}, getClass().getClassLoader())) {
            Class<?> libraryType = loader.loadClass("me.RonanCraft.BetterClaims.lib.folialib.FoliaLib");
            Object library = libraryType.getConstructor(Plugin.class).newInstance(plugin);
            assertEquals(false, libraryType.getMethod("isFolia").invoke(library));
            Object implementation = libraryType.getMethod("getScheduler").invoke(library);
            assertTrue(implementation.getClass().getName().endsWith("LegacySpigotImplementation"));
            AtomicInteger ran = new AtomicInteger();
            implementation.getClass().getMethod("runNextTick", java.util.function.Consumer.class)
                    .invoke(implementation, (java.util.function.Consumer<Object>) ignored -> ran.incrementAndGet());
            assertEquals(0, ran.get());
            pending.get(0).run();
            assertEquals(1, ran.get());
        }
    }
}
