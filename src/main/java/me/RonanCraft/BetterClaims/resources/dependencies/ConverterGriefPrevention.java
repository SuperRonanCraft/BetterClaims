package me.RonanCraft.BetterClaims.resources.dependencies;

import me.RonanCraft.BetterClaims.BetterClaims;
import me.RonanCraft.BetterClaims.claims.data.BoundingBox;
import me.RonanCraft.BetterClaims.claims.ClaimData;
import me.RonanCraft.BetterClaims.claims.ClaimHandler;
import me.RonanCraft.BetterClaims.claims.enums.CLAIM_ERRORS;
import me.RonanCraft.BetterClaims.resources.messages.MessagesCore;
import me.RonanCraft.BetterClaims.resources.helper.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;
import java.util.UUID;

public class ConverterGriefPrevention {

    public static boolean pathExist() {
        return getFile().exists();
    }

    private static File getFile() {
        File path = BetterClaims.getInstance().getDataFolder().getParentFile();
        return new File(path, "GriefPreventionData" + File.separator + "ClaimData");
    }

    public void load(CommandSender converter) {
        File file = getFile();
        if (!file.exists() || file.listFiles() == null)
            return;
        for (File f : Objects.requireNonNull(file.listFiles(filter()))) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
                BoundingBox position = getPosition(config);
                UUID uuid = getID(config);
                ClaimData claimData;
                claimData = HelperClaim.createClaimMain(position, uuid, null, uuid == null);
                CLAIM_ERRORS error = HelperClaim.getHandler().uploadCreatedClaim(claimData, null, null);
                if (error != CLAIM_ERRORS.NONE) {
                    BetterClaims.getInstance().getLogger().warning(error.getMsg(claimData));
                    //error.sendMsg(converter, claim);
                    converter.sendMessage(MessagesCore.CONVERT_FAILED.get(null).replace("%claim%", f.getName()).replace("%plugin%", "GriefPrevention"));
                    BetterClaims.getInstance().getLogger().severe(error.getMsg(claimData));
                } else {
                    converter.sendMessage(MessagesCore.CONVERT_SUCCESS.get(null).replace("%claim%", f.getName()).replace("%plugin%", "GriefPrevention"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                converter.sendMessage(MessagesCore.CONVERT_FAILED.get(null).replace("%claim%", f.getName()).replace("%plugin%", "GriefPrevention"));
                e.printStackTrace();
            }
        }
    }

    private BoundingBox getPosition(YamlConfiguration config) {
        String[] lesser_boundary_corner = Objects.requireNonNull(config.getString("Lesser Boundary Corner")).split(";");
        String[] greater_boundary_corner = Objects.requireNonNull(config.getString("Greater Boundary Corner")).split(";");
        World world = Bukkit.getWorld(lesser_boundary_corner[0]);
        int x1 = Integer.parseInt(lesser_boundary_corner[1]);
        int z1 = Integer.parseInt(lesser_boundary_corner[3]);
        int x2 = Integer.parseInt(greater_boundary_corner[1]);
        int z2 = Integer.parseInt(greater_boundary_corner[3]);
        return new BoundingBox(world, x1, z1, x2, z2);
    }

    private UUID getID(YamlConfiguration config) {
        try {
            return UUID.fromString(Objects.requireNonNull(config.getString("Owner")));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private FilenameFilter filter() {
        return (dir, name) -> name.endsWith(".yml");
    }
}
