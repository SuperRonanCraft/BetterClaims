package me.RonanCraft.BetterClaims.resources.visualization;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
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
        if (!player.isOnline() || HelperPlayer.getData(player).getVisualization() != visualization) return;
        //for each element (=block) of the visualization
        for (int i = 0; i < visualization.elements.size(); i++) {
            VisualizationElement element = visualization.elements.get(i);

            //send the player a fake block change event
            if (!element.sampled || !player.getWorld().equals(element.location.getWorld())
                    || player.getLocation().distanceSquared(element.location) > 100 * 100) continue;
            try {
                element.fakeBlock.send(player, element.location);
                element.applied = true;
            } catch (Exception e) {
                //e.printStackTrace();

                //Class<? extends Player> pClass = player.getClass();
                //for (Field f : pClass.getDeclaredFields())
                //    BetterClaims.getInstance().getLogger().info(f.getType().getCanonicalName() + " " + f.getName());
            }
        }

        //remember the visualization applied to this player for later (so it can be inexpensively reverted)
        HelperPlayer.getData(player).setVisualization(visualization);

        //schedule automatic visualization reversion in 60 seconds.
        BetterClaims.getInstance().getTasks().later(
                player,
                new VisualizationTaskRevert(player, visualization),
                20L * 60);  //60 seconds
    }
}
