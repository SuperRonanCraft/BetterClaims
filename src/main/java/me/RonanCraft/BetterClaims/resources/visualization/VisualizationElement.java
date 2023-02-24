package me.RonanCraft.BetterClaims.resources.visualization;

import org.bukkit.Location;

public class VisualizationElement {

    Location location;
    BlockData fakeBlock;
    BlockData realBlock;

    public VisualizationElement(Location location, BlockData visualizedBlock, BlockData realBlock){
        this.location = location;
        this.fakeBlock = visualizedBlock;
        this.realBlock = realBlock;
    }
}
