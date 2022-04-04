package me.RonanCraft.Pueblos.resources.tools.visual;

import me.RonanCraft.Pueblos.Pueblos;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

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
            try {
                player.sendBlockChange(element.location, element.fakeBlock.mat, (byte) 0);
            } catch (Exception e) {
                e.printStackTrace();

                Class<? extends Player> pClass = player.getClass();
                for (Field f : pClass.getDeclaredFields())
                    Pueblos.getInstance().getLogger().info(f.getType().getCanonicalName() + " " + f.getName());
            }
        }

        //remember the visualization applied to this player for later (so it can be inexpensively reverted)
        Pueblos.getInstance().getPlayerData(player).setVisualization(visualization);

        //schedule automatic visualization reversion in 60 seconds.
        Pueblos.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                Pueblos.getInstance(),
                new VisualizationTaskRevert(player, visualization),
                20L * 60);  //60 seconds
    }
}
