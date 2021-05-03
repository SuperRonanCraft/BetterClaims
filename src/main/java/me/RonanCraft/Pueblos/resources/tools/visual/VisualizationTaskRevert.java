package me.RonanCraft.Pueblos.resources.tools.visual;

import me.RonanCraft.Pueblos.Pueblos;
import org.bukkit.entity.Player;

class VisualizationTaskRevert implements Runnable {
    private final Visualization visualization;
    private final Player player;

    public VisualizationTaskRevert(Player player, Visualization visualization)
    {
        this.visualization = visualization;
        this.player = player;
    }

    @Override
    public void run()
    {
        //don't do anything if the player's current visualization is different from the one scheduled to revert
        if (Pueblos.getInstance().getPlayerData(player).getVisualization() != visualization) return;

        // alert plugins of a visualization
        //Bukkit.getPluginManager().callEvent(new VisualizationEvent(player, null, Collections.<Claim>emptySet()));

        this.visualization.revert(player);
    }
}
