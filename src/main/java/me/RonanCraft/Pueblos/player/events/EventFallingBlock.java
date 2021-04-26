package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class EventFallingBlock implements PueblosEvents {

    private final EventListener listener;
    private final HashMap<Entity, Integer> damageCooldown = new HashMap<>();

    EventFallingBlock(EventListener listener) {
        this.listener = listener;
    }

    public void onEntityChangeBLock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN) {
            event.setCancelled(true);
        } else if (event.getEntityType() == EntityType.SILVERFISH) {
            event.setCancelled(true);
        } else if (event.getEntityType() == EntityType.RABBIT) {
            event.setCancelled(true);
        } else if (event.getEntityType() == EntityType.WITHER) {
            Claim claim = getClaim(event.getBlock().getLocation());
            if (claim == null)
                event.setCancelled(true);
        } else if (event.getTo() == Material.DIRT && event.getBlock().getType() == Material.FARMLAND) { //don't allow crops to be trampled, except by a player with build permission
            if (event.getEntityType() != EntityType.PLAYER) {
                event.setCancelled(true);
            } else {
                Player player = (Player) event.getEntity();
                Block block = event.getBlock();
                if (allowBreak(player, block.getLocation()))
                    event.setCancelled(true);
            }
        }
        //Prevent breaking lilypads via collision with a boat. Thanks Jikoo.
        else if (event.getEntity() instanceof Vehicle && !event.getEntity().getPassengers().isEmpty()) {
            Entity driver = event.getEntity().getPassengers().get(0);
            if (driver instanceof Player) {
                Block block = event.getBlock();
                if (allowBreak((Player) driver, block.getLocation()))
                    event.setCancelled(true);
            }
        }
        //sand cannon fix - when the falling block doesn't fall straight down, take additional anti-grief steps
        else if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock entity = (FallingBlock) event.getEntity();
            Block block = event.getBlock();

            //if changing a block TO air, this is when the falling block formed.  note its original location
            if (event.getTo() == Material.AIR) {
                entity.setMetadata("PUEBLOS_FALLINGBLOCK", new FixedMetadataValue(getPl(), block.getLocation()));
            }
            //otherwise, the falling block is forming a block.  compare new location to original source
            else {
                List<MetadataValue> values = entity.getMetadata("PUEBLOS_FALLINGBLOCK");
                //if we're not sure where this entity came from (maybe another plugin didn't follow the standard?), allow the block to form
                //Or if entity fell through an end portal, allow it to form, as the event is erroneously fired twice in this scenario.
                if (values.size() < 1) return;

                Location originalLocation = (Location) (values.get(0).value());
                Location newLocation = block.getLocation();

                //if did not fall straight down
                assert originalLocation != null;
                if (originalLocation.getBlockX() != newLocation.getBlockX() || originalLocation.getBlockZ() != newLocation.getBlockZ()) {
                    //in other worlds, if landing in land claim, only allow if source was also in the land claim
                    Claim claim = getClaim(newLocation);
                    if (claim != null && !claim.contains(originalLocation)) {
                        //when not allowed, drop as item instead of forming a block
                        event.setCancelled(true);

                        ItemStack itemStack = new ItemStack(entity.getBlockData().getMaterial(), 1);
                        Item item = block.getWorld().dropItem(entity.getLocation(), itemStack);
                        item.setVelocity(new Vector());
                    }
                }
            }
        }
    }

}
