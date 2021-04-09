package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.resources.claims.*;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("ALL")
public class JSONEncoding {

    //String gets
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
            e.printStackTrace();
            return null;
        }
    }

    public static String getJsonFromMembers(List<ClaimMember> members) {
        if (members == null)
            return null;
        List<Map> array = new ArrayList<>();
        for (ClaimMember member : members) {
            if (member.owner)
                continue;
            HashMap<String, Object> obj = new HashMap();
            obj.put("uuid", member.uuid.toString());
            obj.put("name", member.name);
            obj.put("date", new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(member.date));
            HashMap<String, Object> obj2 = new HashMap<>();
            for (Map.Entry<CLAIM_FLAG_MEMBER, Object> flag : member.getFlags().entrySet()) {
                obj2.put(flag.getKey().name(), flag.getValue());
            }
            obj.put("flags", obj2);
            array.add(obj);
        }
        //System.out.println(JSONArray.toJSONString(array));
        return JSONArray.toJSONString(array);
    }

    public static String getJsonFromRequests(List<ClaimRequest> requests) {
        if (requests == null)
            return null;
        List<Map> array = new ArrayList<>();
        for (ClaimRequest request : requests) {
            HashMap<String, Object> obj = new HashMap();
            obj.put("uuid", request.id.toString());
            obj.put("name", request.name);
            obj.put("date", new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(request.date));
            array.add(obj);
        }
        //System.out.println(JSONArray.toJSONString(array));
        return JSONArray.toJSONString(array);
    }

    //Object gets
    public static List<ClaimRequest> getRequests(String json, Claim claim) {
        if (json == null)
            return null;
        try {
            JSONArray obj = (JSONArray) JSONValue.parse(json);
            List<ClaimRequest> requests = new ArrayList<>();
            for (Object o : obj) {
                Map member_info = (Map) o;
                String uuid = member_info.get("uuid").toString();
                String name = member_info.get("name").toString();
                String date = member_info.get("date").toString();
                ClaimRequest request = new ClaimRequest(UUID.fromString(uuid), name, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(date));
                requests.add(request);
            }
            return requests;
        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ClaimMember> getMember(String json, Claim claim) {
        if (json == null)
            return null;
        try {
            JSONArray obj = (JSONArray) JSONValue.parse(json);
            List<ClaimMember> members = new ArrayList<>();
            for (Object o : obj) {
                Map member_info = (Map) o;
                String uuid = member_info.get("uuid").toString();
                String name = member_info.get("name").toString();
                String date = member_info.get("date").toString();
                HashMap<CLAIM_FLAG_MEMBER, Object> flags = new HashMap<>();
                for (Map.Entry<String, Object> flag : ((HashMap<String, Object>) member_info.get("flags")).entrySet()) {
                    CLAIM_FLAG_MEMBER _flag = CLAIM_FLAG_MEMBER.valueOf(flag.getKey());
                    Object _value = flag.getValue();
                    flags.put(_flag, _value);
                }
                ClaimMember member = new ClaimMember(UUID.fromString(uuid), name, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(date), false, claim);
                member.setFlag(flags, false);
                members.add(member);
            }
            return members;
        } catch (NullPointerException | ParseException e) {
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