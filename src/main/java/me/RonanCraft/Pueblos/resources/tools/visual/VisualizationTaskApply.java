package me.RonanCraft.Pueblos.resources.tools.visual;

import me.RonanCraft.Pueblos.Pueblos;
import org.bukkit.entity.Player;

class VisualizationTaskApply implements Runnable
{
    private final Visualization visualization;
    private final Player player;

    public VisualizationTaskApply(Player player, Visualization visualization) {
        this.visualization = visualization;
        this.player = player;
    }


    @Override
    public void run() {
        //for each element (=block) of the visualization
        for (int i = 0; i < visualization.elements.size(); i++) {
            VisualizationElement element = visualization.elements.get(i);

            //send the player a fake block change event
            if (!element.location.getChunk().isLoaded()) continue;  //cheap distance check
            player.sendBlockChange(element.location, element.fakeBlock);
        }

        //remember the visualization applied to this player for later (so it can be inexpensively reverted)
        Pueblos.getInstance().getSystems().getPlayerInfo().addVisualization(player, visualization);

        //schedule automatic visualization reversion in 60 seconds.
        Pueblos.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                Pueblos.getInstance(),
                new VisualizationTaskRevert(player, visualization),
                20L * 60);  //60 seconds
    }
}
