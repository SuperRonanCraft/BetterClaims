package me.RonanCraft.Pueblos.resources.tools;

import org.bukkit.entity.Player;

public class Confirmation {

    public final CONFIRMATION_TYPE type;
    public final Player p;
    public final Object info;

    public Confirmation(CONFIRMATION_TYPE type, Player p, Object info) {
        this.type = type;
        this.p = p;
        this.info = info;
    }


}
