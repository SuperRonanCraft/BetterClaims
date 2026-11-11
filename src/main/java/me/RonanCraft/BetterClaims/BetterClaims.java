/*
 * Copyright (c) 2021 RonanCraft Network
 * All rights reserved.
 */

package me.RonanCraft.BetterClaims;

import com.ronanplugins.bettersync.editor.EditorClient;
import com.ronanplugins.bettersync.editor.EditorSchema;


import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.database.DatabaseAuctions;
import me.RonanCraft.BetterClaims.database.DatabaseClaims;
import me.RonanCraft.BetterClaims.inventory.ClaimInvLoader;
import me.RonanCraft.BetterClaims.inventory.ClaimInventory;
import me.RonanCraft.BetterClaims.inventory.GlobalItems;
import me.RonanCraft.BetterClaims.player.command.Commands;
import me.RonanCraft.BetterClaims.player.data.PlayerData;
import me.RonanCraft.BetterClaims.player.events.EventListener;
import me.RonanCraft.BetterClaims.resources.Permissions;
import me.RonanCraft.BetterClaims.resources.Settings;
import me.RonanCraft.BetterClaims.resources.Systems;
import me.RonanCraft.BetterClaims.resources.files.Files;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BetterClaims extends JavaPlugin {
    private static BetterClaims instance;
    private EditorClient editor;

    public EditorClient getEditor() { return editor; }

    private final Files files = new Files();
    private final Permissions permissions = new Permissions();
    private final Commands commands = new Commands();
    private final Systems systems = new Systems();
    //@Getter private final DynMap dynMap = new DynMap();
    private boolean PlaceholderAPI;

    private me.RonanCraft.BetterClaims.platform.PlatformTasks tasks;
    public me.RonanCraft.BetterClaims.platform.PlatformTasks getTasks() { return tasks; }

    @Override
    public void onEnable() {
        instance = this;
        tasks = new me.RonanCraft.BetterClaims.platform.PlatformTasks(this);
        loadAll(false);
        editor = new EditorClient(this,
                EditorSchema.load(this, "META-INF/editor-schema.json"),
                EditorClient.website(this, "META-INF/betterclaims-editor.properties"),
                tasks::player, this::reload,
                sender -> !(sender instanceof Player) || sender.hasPermission("betterclaims.editor"),
                () -> me.RonanCraft.BetterClaims.resources.files.FileOther.FILETYPE.CONFIG.getConfig().saveToString());
        new Updater(this);
    }

    @Override
    public void onDisable() {
        if (editor != null) editor.close();
        if (tasks != null) {
            synchronized (tasks.stateLock()) {
                // Legacy servers permit closing menus during disable on their main thread.
                if (!tasks.isFolia())
                    for (Player player : Bukkit.getOnlinePlayers())
                        if (getPlayerData(player).getInventory() != null) player.closeInventory();
                tasks.close();
                systems.getDatabaseClaims().saveChanges();
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sendi, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            commands.commandExecuted(sendi, label, args);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sendi, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            return commands.onTabComplete(sendi, args);
        }
    }

    public void reload(CommandSender sendi) {
        synchronized (BetterClaims.getInstance().getTasks().stateLock()) {
            systems.getDatabaseClaims().saveChanges();
            closeMenus();
            loadAll(true);
            MessagesCore.RELOAD.send(sendi);
        }
    }

    //(Re)Load all plugin systems/files/cache
    private void loadAll(boolean reload) {
        registerDependencies();
        files.loadAll();
        systems.load(reload);
        commands.load();
        permissions.register();
        for (ClaimInventory inv : ClaimInventory.values())
            if (inv.get() instanceof ClaimInvLoader)
                ((ClaimInvLoader) inv.get()).load();
    }

    private void closeMenus() {
        for (Player plr : Bukkit.getOnlinePlayers())
            if (getPlayerData(plr).getInventory() != null)
                tasks.entity(plr, plr::closeInventory);
    }

    private void registerDependencies() {
        PlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static BetterClaims getInstance() {
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

    public Settings getSettings() {
        return systems.getSettings();
    }

    public DatabaseClaims getDatabaseClaims() {
        return systems.getDatabaseClaims();
    }

    public DatabaseAuctions getDatabaseAuctions() {
        return systems.getDatabaseAuctions();
    }

    public ClaimHandler getClaimHandler() {
        return systems.getClaimHandler();
    }

    public EventListener getEvents() {
        return systems.getEvents();
    }

    public void clearPlayerData(Player p) {
        systems.clearPlayerData(p);
    }

    public PlayerData getPlayerData(Player p) {
        return systems.getPlayerData(p);
    }

    public GlobalItems getGlobalItems() {
        return systems.getGlobalItems();
    }

    public boolean papiEnabled() {
        return PlaceholderAPI;
    }
}
