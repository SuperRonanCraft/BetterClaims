package me.RonanCraft.Pueblos;

import me.RonanCraft.Pueblos.events.command.Commands;
import me.RonanCraft.Pueblos.resources.Permissions;
import me.RonanCraft.Pueblos.resources.Systems;
import me.RonanCraft.Pueblos.resources.files.Files;
import me.RonanCraft.Pueblos.resources.files.msgs.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Pueblos extends JavaPlugin {
    private static Pueblos instance;
    private final Files files = new Files();
    private final Permissions permissions = new Permissions();
    private final Commands commands = new Commands();
    private final Systems systems = new Systems();

    @Override
    public void onEnable() {
        instance = this;
        loadAll();
        systems.getEvents().load();
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
        loadAll();
        Messages.core.sendReload(sendi);
    }

    //(Re)Load all plugin systems/files/cache
    private void loadAll() {
        files.loadAll();
        commands.load();
        systems.load();
        permissions.register();
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
}
