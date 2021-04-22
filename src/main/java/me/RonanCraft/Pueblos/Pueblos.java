package me.RonanCraft.Pueblos;

import me.RonanCraft.Pueblos.inventory.PueblosInvLoader;
import me.RonanCraft.Pueblos.inventory.PueblosInventory;
import me.RonanCraft.Pueblos.player.command.Commands;
import me.RonanCraft.Pueblos.resources.Permissions;
import me.RonanCraft.Pueblos.resources.Systems;
import me.RonanCraft.Pueblos.resources.files.Files;
import me.RonanCraft.Pueblos.resources.files.msgs.MessagesCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Pueblos extends JavaPlugin {
    private static Pueblos instance;
    private final Files files = new Files();
    private final Permissions permissions = new Permissions();
    private final Commands commands = new Commands();
    private final Systems systems = new Systems();
    private boolean PlaceholderAPI;

    @Override
    public void onEnable() {
        instance = this;
        loadAll(false);
        new Updater(this);
    }

    @Override
    public void onDisable() {
        closeMenus();
        systems.getClaimDatabase().saveChanges();
    }

    @Override
    public boolean onCommand(CommandSender sendi, Command command, String label, String[] args) {
        commands.commandExecuted(sendi, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sendi, Command command, String alias, String[] args) {
        return commands.onTabComplete(sendi, args);
    }

    public void reload(CommandSender sendi) {
        systems.getClaimDatabase().saveChanges();
        closeMenus();
        loadAll(true);
        MessagesCore.RELOAD.send(sendi);
    }

    //(Re)Load all plugin systems/files/cache
    private void loadAll(boolean reload) {
        registerDependencies();
        files.loadAll();
        systems.load();
        systems.getEvents().load(reload);
        commands.load();
        permissions.register();
        for (PueblosInventory inv : PueblosInventory.values())
            if (inv.get() instanceof PueblosInvLoader)
                ((PueblosInvLoader) inv.get()).load();
    }

    private void closeMenus() {
        for (Player plr : Bukkit.getOnlinePlayers())
            if (systems.getPlayerInfo().getInventory(plr) != null)
                plr.closeInventory();
    }

    private void registerDependencies() {
        PlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static Pueblos getInstance() {
        return instance;
    }

    public Files getFiles() {
        return files;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public Commands getCmd() {
        return commands;
    }

    public Systems getSystems() {
        return systems;
    }

    public boolean papiEnabled() {
        return PlaceholderAPI;
    }
}
