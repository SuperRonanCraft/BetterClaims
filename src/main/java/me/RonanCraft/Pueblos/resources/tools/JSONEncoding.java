package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.claims.ClaimPosition;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class JSONEncoding {

    public static String getJsonFromList(String id, List<String> list) {
        if (list == null)
            return null;
        JSONObject obj = new JSONObject();
        obj.put(id, list);
        //System.out.print(obj.toString());
        return obj.toString();
    }

    public static String getJsonFromClaim(ClaimPosition position) {
        if (position == null)
            return null;
        Map obj = new LinkedHashMap();
        obj.put("world", position.getWorld().getName());
        obj.put("x_1", position.getX_1());
        obj.put("z_1", position.getZ_1());
        obj.put("x_2", position.getX_2());
        obj.put("z_2", position.getZ_2());
        String json = JSONObject.toJSONString(obj);
        return json.toString();
    }

    public static ClaimPosition getPosition(String json) {
        try {
            JSONObject obj = (JSONObject) JSONValue.parse(json);
            Map chunk_info = (Map) obj;
            String world = chunk_info.get("world").toString();
            int x_1 = Integer.valueOf(chunk_info.get("x_1").toString());
            int z_1 = Integer.valueOf(chunk_info.get("z_1").toString());
            int x_2 = Integer.valueOf(chunk_info.get("x_2").toString());
            int z_2 = Integer.valueOf(chunk_info.get("z_2").toString());
            return new ClaimPosition(Bukkit.getWorld(world), x_1, z_1, x_2, z_2);
        } catch (NullPointerException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static List<String> getListFromJson(String id, String json) {
        try {
            JSONObject obj = (JSONObject) JSONValue.parse(json);
            return (List<String>) obj.get(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

}