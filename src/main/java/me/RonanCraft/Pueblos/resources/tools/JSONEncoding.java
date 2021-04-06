package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.Systems;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

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

    public static String getJsonFromChunk(List<Chunk> chunks) {
        if (chunks == null)
            return null;
        List<JSONObject> chunk_info = new ArrayList<>();
        for (Chunk chunk : chunks) {
            Map obj = new LinkedHashMap();
            obj.put("x", chunk.getX());
            obj.put("z", chunk.getZ());
            chunk_info.add(new JSONObject(obj));
        }
        String json = JSONArray.toJSONString(chunk_info);
        return json.toString();
    }

    public static List<Chunk> getChunks(String json, World world) {
        try {
            List<Chunk> chunks = new ArrayList<>();
            JSONArray array = (JSONArray) JSONValue.parse(json);
            for (Object obj : array) {
                Map chunk_info = (Map) obj;
                int x = Integer.valueOf(chunk_info.get("x").toString());
                int z = Integer.valueOf(chunk_info.get("z").toString());
                chunks.add(world.getChunkAt(x, z));
            }
            return chunks;
        } catch (NullPointerException e) {
            e.printStackTrace();
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