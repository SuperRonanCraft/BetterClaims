package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.player.command.PueblosCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PueblosEvent_CommandExecuted extends Event {

    private final PueblosCommand cmd;
    //private boolean cancelled;

    private static final HandlerList handler = new HandlerList();

    public PueblosEvent_CommandExecuted(PueblosCommand cmd) {
        this.cmd = cmd;
    }

    public PueblosCommand getCommand() {
        return cmd;
    }

    //Default Stuff
    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    /*@Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }*/
}
