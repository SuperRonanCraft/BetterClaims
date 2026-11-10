package me.RonanCraft.BetterClaims.resources.visualization;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.*;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import me.RonanCraft.BetterClaims.resources.helper.HelperPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Visualization {

    List<VisualizationElement> elements = new ArrayList<>();
    private final List<java.util.concurrent.CompletableFuture<Void>> sampling = new ArrayList<>();

    public void apply(Player player) {
        //if he has any current visualization, clear it first
        if (HelperPlayer.getData(player).getVisualization() != null) {
            revert(player);
        }

        if (!player.isOnline()) return;
        // Record the request before region sampling finishes; a newer request supersedes it.
        HelperPlayer.getData(player).setVisualization(this);
        java.util.concurrent.CompletableFuture.allOf(sampling.toArray(new java.util.concurrent.CompletableFuture[0]))
            .whenComplete((ignored, failure) -> BetterClaims.getInstance().getTasks().later(player, () -> {
                if (HelperPlayer.getData(player).getVisualization() != this) return;
                if (failure != null) {
                    HelperPlayer.getData(player).removeVisualization();
                    BetterClaims.getInstance().getLogger().log(java.util.logging.Level.WARNING, "Could not sample claim border", failure);
                    return;
                }
                new VisualizationTaskApply(player, this).run();
            }, 5L));
    }

    public void revert(Player player) {
        if (!player.isOnline()) return;

        Visualization visualization = HelperPlayer.getData(player).getVisualization();

        if (visualization != null) {
            //locality
            int minx = player.getLocation().getBlockX() - 100;
            int minz = player.getLocation().getBlockZ() - 100;
            int maxx = player.getLocation().getBlockX() + 100;
            int maxz = player.getLocation().getBlockZ() + 100;

            //remove any elements which are too far away
            List<VisualizationElement> visible;
            synchronized (visualization.elements) { visible = new ArrayList<>(visualization.elements); }
            visualization.removeElementsOutOfRange(visible, minx, minz, maxx, maxz);

            //send real block information for any remaining elements
            for (int i = 0; i < visible.size(); i++) {
                VisualizationElement element = visible.get(i);
                if (!element.applied) continue;

                if (!player.getWorld().equals(element.location.getWorld())) continue;

                try {
                    element.realBlock.send(player, element.location);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            HelperPlayer.getData(player).removeVisualization();
        }
    }

    //convenience method to build a visualization from a claim
    //visualizationType determines the style (gold blocks, silver, red, diamond, etc)
    public static Visualization fromClaim(ClaimData claimData, int height, VisualizationType visualizationType, Location locality) {
        //System.out.println("Visualizing! " + new Random().nextInt());
        //visualize only top level claims
        /*if (claim.parent != null) {
            return FromClaim(claim.parent, height, visualizationType, locality);
        }*/

        Visualization visualization = new Visualization();

        //add subdivisions first

        if (claimData instanceof Claim_Child) {
            claimData = ((Claim_Child) claimData).parent;
            visualizationType = VisualizationType.CLAIM;
        }

        List<Claim_Child> children = HelperClaim.getHandler().getClaimsChild((Claim) claimData);
        for (Claim_Child child : children) {
            VisualizationType type = VisualizationType.CLAIM_SUB;
            if (child.getAuction() != null)
                type = VisualizationType.AUCTION;
            visualization.addElements(child.getLesserBoundaryCorner(), child.getGreaterBoundaryCorner(), height, type, locality);
        }

        //special visualization for administrative land claims
        if (visualizationType == VisualizationType.CLAIM && claimData.isAdminClaim())
            visualizationType = VisualizationType.ADMIN_CLAIM;
        //}

        //add top level last so that it takes precedence (it shows on top when the child claim boundaries overlap with its boundaries)
        visualization.addElements(claimData.getLesserBoundaryCorner(), claimData.getGreaterBoundaryCorner(), height, visualizationType, locality);

        return visualization;
    }

    //convenience method to build a visualization from a location
    //visualizationType determines the style (gold blocks, silver, red, diamond, etc)
    public static Visualization fromLocation(Visualization visualization, Location location, int height, Location locality) {
        //add top level last so that it takes precedence (it shows on top when the child claim boundaries overlap with its boundaries)
        visualization.addSides(location, location, locality, height, new BlockData(Material.EMERALD_BLOCK), new BlockData(Material.EMERALD_BLOCK), 1);

        return visualization;
    }

    public static Visualization fromLocation(Location location, int height, Location locality) {
        return fromLocation(new Visualization(), location, height, locality);
    }

    public static Visualization fromLocation(Location min, Location max, int height, VisualizationType visualizationType, Location locality) {

        Visualization visualization = new Visualization();

        //add top level last so that it takes precedence (it shows on top when the child claim boundaries overlap with its boundaries)
        visualization.addElements(min, max, height, visualizationType, locality);

        return visualization;
    }

    //adds a claim's visualization to the current visualization
    //handy for combining several visualizations together, as when visualization a top level claim with several subdivisions inside
    //locality is a performance consideration.  only create visualization blocks for around 100 blocks of the locality

    private void addElements(Location min, Location max, int height, VisualizationType visualizationType, Location locality) {
        BlockData cornerBlockData = new BlockData(Material.GLOWSTONE);//.createBlockData();
        BlockData accentBlockData;

        switch (visualizationType) {
            case CLAIM: accentBlockData = new BlockData(Material.EMERALD_BLOCK); break;
            case ADMIN_CLAIM: accentBlockData = new BlockData(Material.COAL_BLOCK); break;
            case CLAIM_SUB: accentBlockData = new BlockData(Material.GOLD_BLOCK); break;
            case EDIT: accentBlockData = new BlockData(Material.IRON_BLOCK); break;
            case AUCTION: accentBlockData = new BlockData(Material.DIAMOND_BLOCK); break;
            default: //Errors
                //((Lightable) cornerBlockData).setLit(true); //For redstone
                accentBlockData = new BlockData(Material.NETHERRACK);
                break;
        }
        int step;
        switch (visualizationType) {
            case ERROR_LARGE:
            case ERROR_SMALL: step = 1; break;
            case EDIT: step = 5; break;
            default: step = 10;
        }
        addSides(min, max, locality, height, cornerBlockData, accentBlockData, step);
    }

    //adds a general claim cuboid (represented by min and max) visualization to the current visualization
    void addSides(Location min, Location max, Location locality, int height, BlockData cornerBlockData, BlockData accentBlockData, int STEP) {
        World world = min.getWorld();
        boolean waterIsTransparent = BetterClaims.getInstance().getTasks().owns(locality)
                && locality.getBlock().getType() == Material.WATER;

        int smallx = min.getBlockX();
        int smallz = min.getBlockZ();
        int bigx = max.getBlockX();
        int bigz = max.getBlockZ();

        ArrayList<VisualizationElement> newElements = new ArrayList<>();

        //initialize visualization elements without Y values and real data
        //that will be added later for only the visualization elements within visualization range

        //locality
        int minx = locality.getBlockX() - 75;
        int minz = locality.getBlockZ() - 75;
        int maxx = locality.getBlockX() + 75;
        int maxz = locality.getBlockZ() + 75;

        BlockData realBlock = new BlockData(Material.AIR);
        //top line
        newElements.add(new VisualizationElement(new Location(world, smallx, 0, bigz), cornerBlockData, realBlock));
        newElements.add(new VisualizationElement(new Location(world, smallx + 1, 0, bigz), accentBlockData, realBlock));
        for (int x = smallx + STEP; x < bigx - STEP / 2; x += STEP) {
            if (x > minx && x < maxx)
                newElements.add(new VisualizationElement(new Location(world, x, 0, bigz), accentBlockData, realBlock));
        }
        newElements.add(new VisualizationElement(new Location(world, bigx - 1, 0, bigz), accentBlockData, realBlock));

        //bottom line
        newElements.add(new VisualizationElement(new Location(world, smallx + 1, 0, smallz), accentBlockData, realBlock));
        for (int x = smallx + STEP; x < bigx - STEP / 2; x += STEP) {
            if (x > minx && x < maxx)
                newElements.add(new VisualizationElement(new Location(world, x, 0, smallz), accentBlockData, realBlock));
        }
        newElements.add(new VisualizationElement(new Location(world, bigx - 1, 0, smallz), accentBlockData, realBlock));

        //left line
        newElements.add(new VisualizationElement(new Location(world, smallx, 0, smallz), cornerBlockData, realBlock));
        newElements.add(new VisualizationElement(new Location(world, smallx, 0, smallz + 1), accentBlockData, realBlock));
        for (int z = smallz + STEP; z < bigz - STEP / 2; z += STEP) {
            if (z > minz && z < maxz)
                newElements.add(new VisualizationElement(new Location(world, smallx, 0, z), accentBlockData, realBlock));
        }
        newElements.add(new VisualizationElement(new Location(world, smallx, 0, bigz - 1), accentBlockData, realBlock));

        //right line
        newElements.add(new VisualizationElement(new Location(world, bigx, 0, smallz), cornerBlockData, realBlock));
        newElements.add(new VisualizationElement(new Location(world, bigx, 0, smallz + 1), accentBlockData, realBlock));
        for (int z = smallz + STEP; z < bigz - STEP / 2; z += STEP) {
            if (z > minz && z < maxz)
                newElements.add(new VisualizationElement(new Location(world, bigx, 0, z), accentBlockData, realBlock));
        }
        newElements.add(new VisualizationElement(new Location(world, bigx, 0, bigz - 1), accentBlockData, realBlock));
        newElements.add(new VisualizationElement(new Location(world, bigx, 0, bigz), cornerBlockData, realBlock));

        //remove any out of range elements
        this.removeElementsOutOfRange(newElements, minx, minz, maxx, maxz);

        //remove any elements outside the claim
        BoundingBox box = new BoundingBox(min, max);
        for (int i = 0; i < newElements.size(); i++) {
            VisualizationElement element = newElements.get(i);
            if (!containsIncludingIgnoringHeight(box, element.location.toVector())) {
                newElements.remove(i--);
            }
        }

        // Read terrain on the region that owns each column, then publish a completed snapshot.
        final int sampleHeight = height;
        for (VisualizationElement element : newElements) {
            elements.add(element); // Preserve parent-over-child ordering regardless of region completion order.
            java.util.concurrent.CompletableFuture<Void> ready = new java.util.concurrent.CompletableFuture<>();
            sampling.add(ready);
            BetterClaims.getInstance().getTasks().location(element.location, () -> {
                try {
                    Location loc = element.location;
                    if (world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                        element.location = getVisibleLocation(world, loc.getBlockX(), sampleHeight, loc.getBlockZ(), waterIsTransparent);
                        element.realBlock = new BlockData(element.location.getBlock());
                        element.sampled = true;
                    }
                    ready.complete(null);
                } catch (Exception error) {
                    ready.completeExceptionally(error);
                }
            });
        }
    }

    //finds a block the player can probably see.  this is how visualizations "cling" to the ground or ceiling
    private static Location getVisibleLocation(World world, int x, int y, int z, boolean waterIsTransparent) {
        int minimum = 0;
        try { minimum = (Integer) World.class.getMethod("getMinHeight").invoke(world); }
        catch (NoSuchMethodException ignored) { /* Before extended world height. */ }
        catch (ReflectiveOperationException error) { throw new IllegalStateException(error); }
        y = Math.max(minimum, Math.min(y, world.getMaxHeight() - 1));
        Block block = world.getBlockAt(x, y, z);
        BlockFace direction = (isTransparent(block, waterIsTransparent)) ? BlockFace.DOWN : BlockFace.UP;

        while (block.getY() > minimum &&
                block.getY() < world.getMaxHeight() - 1 &&
                (!isTransparent(block.getRelative(BlockFace.UP), waterIsTransparent) || isTransparent(block, waterIsTransparent)))
        {
            block = block.getRelative(direction);
        }

        return block.getLocation();
    }

    //helper method for above.  allows visualization blocks to sit underneath partly transparent blocks like grass and fence
    private static boolean isTransparent(Block block, boolean waterIsTransparent) {
        Material blockMaterial = block.getType();
        //Blacklist
        if (blockMaterial == Material.SNOW) return false;

        //Whitelist TODO: some of this might already be included in isTransparent()
        if (blockMaterial == Material.AIR || blockMaterial.name().contains("FENCE"))
            return true;

        return (waterIsTransparent && block.getType() == Material.WATER) ||
                block.getType().isTransparent();
    }

    private boolean containsIncludingIgnoringHeight(BoundingBox box, Vector vector) {
        return vector.getBlockX() >= box.getMinX()
                && vector.getBlockX() <= box.getMaxX()
                && vector.getBlockZ() >= box.getMinZ()
                && vector.getBlockZ() <= box.getMaxZ();
    }

    //removes any elements which are out of visualization range
    private void removeElementsOutOfRange(List<VisualizationElement> elements, int minx, int minz, int maxx, int maxz) {
        for (int i = 0; i < elements.size(); i++) {
            Location location = elements.get(i).location;
            if (location.getX() < minx || location.getX() > maxx || location.getZ() < minz || location.getZ() > maxz) {
                elements.remove(i--);
            }
        }
    }

}
