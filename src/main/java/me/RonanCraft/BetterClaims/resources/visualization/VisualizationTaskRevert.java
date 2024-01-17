package me.RonanCraft.BetterClaims.resources.visualization;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
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
        if (HelperPlayer.getData(player).getVisualization() != visualization) return;

        // alert plugins of a visualization
        //Bukkit.getPluginManager().callEvent(new VisualizationEvent(player, null, Collections.<Claim>emptySet()));

        this.visualization.revert(player);
    }
}
