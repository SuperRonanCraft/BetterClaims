package me.RonanCraft.BetterClaims.player.events;

import me.RonanCraft.BetterClaims.resources.Settings;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

public class EventFallingBlock implements ClaimEvents {

    private boolean getSetting(Settings.SETTING set) {
        return getPl().getSettings().getBoolean(set);
    }

    //Totally not taken from GriefPrevention O_O
    //Credit: (https://github.com/TechFortress/GriefPrevention/blob/master/src/main/java/me/ryanhamshire/GriefPrevention/EntityEventHandler.java)

    public void onEntityChangeBLock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN) { //If set to false, disable endermen picking up blocks
            if (!getSetting(Settings.SETTING.GLOBAL_ENDERMEN_MOVEBLOCKS))
                event.setCancelled(true);
        } else if (event.getEntityType() == EntityType.SILVERFISH) { //If set to false, disable silverfish going into blocks
            if (!getSetting(Settings.SETTING.GLOBAL_SILVERFISH_BREAKBLOCKS))
                event.setCancelled(true);
        } else if (event.getEntityType() == EntityType.RABBIT) { //If set to false, disable rabbits messing up crops
            if (!getSetting(Settings.SETTING.GLOBAL_RABBITS_EATCROPS))
                event.setCancelled(true);
        } else if (event.getEntityType() == EntityType.WITHER) { //Allow withers to destroy blocks OUTSIDE claims
            ClaimData claimData = getClaimAt(event.getBlock().getLocation(), true);
            if (claimData == null)
                event.setCancelled(true);
        } else if (event.getTo() == Material.DIRT ) {//&& event.getBlock().getType() == Material.FARMLAND) { //don't allow crops to be trampled, except by a player with build permission
            if (event.getEntityType() != EntityType.PLAYER) {
                event.setCancelled(true);
            } else {
                Player player = (Player) event.getEntity();
                Block block = event.getBlock();
                if (allowBreak(player, block.getLocation()))
                    event.setCancelled(true);
            }
        }
        //Prevent breaking lilypads via collision with a boat.
        else if (event.getEntity() instanceof Vehicle && !event.getEntity().getPassenger().isEmpty()) {
            Entity driver = event.getEntity().getPassenger();//.getPassengers().get(0);
            if (driver instanceof Player) {
                Block block = event.getBlock();
                if (allowBreak((Player) driver, block.getLocation()))
                    event.setCancelled(true);
            }
        } else if (event.getEntityType() == EntityType.FALLING_BLOCK) { //Prevent falling blocks from landing into claims from the wilderness
            tntCannons(event);
        }
    }

    //Sand Cannons - When falling blocks don't fall straight down, save meta-data to check previous and current position
    private void tntCannons(EntityChangeBlockEvent event) {
        FallingBlock entity = (FallingBlock) event.getEntity();
        Block block = event.getBlock();

        //if changing a block TO air, this is when the falling block formed.  note its original location
        if (event.getTo() == Material.AIR) {
            entity.setMetadata("BETTERCLAIMS_FALLINGBLOCK", new FixedMetadataValue(getPl(), block.getLocation()));
        }
        //otherwise, the falling block is forming a block.  compare new location to original source
        else {
            List<MetadataValue> values = entity.getMetadata("BETTERCLAIMS_FALLINGBLOCK");
            //if we're not sure where this entity came from (maybe another plugin didn't follow the standard?), allow the block to form
            //Or if entity fell through an end portal, allow it to form, as the event is erroneously fired twice in this scenario.
            if (values.size() < 1) return;

            Location originalLocation = (Location) (values.get(0).value());
            Location newLocation = block.getLocation();

            //if did not fall straight down
            assert originalLocation != null;
            if (originalLocation.getBlockX() != newLocation.getBlockX() || originalLocation.getBlockZ() != newLocation.getBlockZ()) {
                //in other worlds, if landing in land claim, only allow if source was also in the land claim
                ClaimData claimData = getClaimAt(newLocation, true);
                if (claimData != null && !claimData.contains(originalLocation)) {
                    //when not allowed, drop as item instead of forming a block
                    event.setCancelled(true);

                    ItemStack itemStack = new ItemStack(entity.getMaterial(), 1);
                    Item item = block.getWorld().dropItem(entity.getLocation(), itemStack);
                    item.setVelocity(new Vector());
                }
            }
        }
    }
}
