package me.RonanCraft.BetterClaims.web.editor;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/** Fixed settings retain their schema; supported collections allow typed named entries. */
public final class EditorConfig {
    public static final int MAX_BYTES = 128 * 1024;
    private static final Pattern TOKENS = Pattern.compile("(^|[\\s\\[\\]{},])[!&*]", Pattern.MULTILINE);
    private EditorConfig() { }

    private static String withoutQuotedScalars(String text) {
        // Only complete, single-line quoted scalars: Minecraft color codes are text, not YAML anchors.
        return text.replaceAll("\"(?:\\\\[^\\r\\n]|[^\"\\\\\\r\\n])*\"|'(?:''|[^'\\r\\n])*'", "quoted");
    }

    public static YamlConfiguration parse(String text) throws Exception {
        // Bukkit supports object deserialization through the reserved == key. Reject equals
        // and encoded character escapes before invoking its parser, not only after parsing.
        if (text.indexOf("==") >= 0 || Pattern.compile("\\\\[xuU]").matcher(text).find())
            throw new IOException("Object serialization markers and encoded character escapes are not supported.");
        if (text.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES || TOKENS.matcher(withoutQuotedScalars(text)).find())
            throw new IOException("Configuration too large, or YAML tags/anchors/aliases present.");
        int lines = 0, flow = 0;
        for (String line : text.split("\\n")) {
            if (++lines > 4000 || line.matches("^ {65}.*")) throw new IOException("Configuration is too complex.");
            for (char c : line.toCharArray()) if (c == '[' || c == '{') flow++;
        }
        if (flow > 128) throw new IOException("Too many flow collections.");
        YamlConfiguration parsed = new YamlConfiguration();
        parsed.loadFromString(text);
        for (String section : Arrays.asList("Claim", "Global", "Player", "DefaultFlags"))
            if (!parsed.isConfigurationSection(section)) throw new IOException("Missing BetterClaims section: " + section);
        return parsed;
    }

    public static void validate(String original, String edited) throws Exception {
        YamlConfiguration before = parse(original), after = parse(edited);
        if (!Objects.equals(before.get("Language-File"), after.get("Language-File")))
            throw new IOException("Change Language-File locally, not through the web editor.");
        compare(before, after, "config", 0);
        new java.text.SimpleDateFormat(after.getString("DateFormat"));
        String item = after.getString("Claim.Item");
        if (org.bukkit.Material.matchMaterial(item) == null)
            throw new IOException("Unknown claim item material.");
    }

    private static Object normalize(Object value) {
        return value instanceof ConfigurationSection ? ((ConfigurationSection) value).getValues(false) : value;
    }

    private static void compare(Object oldValue, Object newValue, String path, int depth) throws IOException {
        if (depth > 32) throw new IOException("Configuration nesting is too deep.");
        oldValue = normalize(oldValue); newValue = normalize(newValue);
        if (path.equals("config.DefaultFlags")) {
            if (!(newValue instanceof Map)) throw new IOException("Expected flags section.");
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) newValue).entrySet()) {
                if (!Arrays.asList("PVP", "TNT", "ALLOW_DOOR", "ALLOW_LEVER", "ALLOW_BUTTON", "ALLOW_BED", "ALLOW_REQUESTS", "ALLOW_ITEM_PICKUP").contains(entry.getKey()) || !(entry.getValue() instanceof Boolean))
                    throw new IOException("Invalid default flag.");
            }
            return;
        }
        if (path.equals("config.Claim.WorldsEnabled")) { stringList(newValue, path); return; }
        if (oldValue instanceof Map) {
            if (!(newValue instanceof Map)) throw new IOException("Expected section: " + path);
            Map<?, ?> oldMap = (Map<?, ?>) oldValue, newMap = (Map<?, ?>) newValue;
            if (!oldMap.keySet().equals(newMap.keySet())) throw new IOException("Keep existing setting names: " + path);
            for (Object key : oldMap.keySet()) compare(oldMap.get(key), newMap.get(key), path + "." + key, depth + 1);
        } else if (oldValue instanceof List) {
            if (!(newValue instanceof List)) throw new IOException("Expected list: " + path);
            List<?> oldList = (List<?>) oldValue, newList = (List<?>) newValue;
            if (newList.size() > 1000) throw new IOException("List too long: " + path);
            boolean structured = oldList.stream().anyMatch(v -> normalize(v) instanceof Map);
            if (structured && oldList.size() != newList.size()) throw new IOException("Keep existing world/group entries: " + path);
            for (int i = 0; i < newList.size(); i++) {
                Object baseline = structured ? oldList.get(i) : oldList.isEmpty() ? "" : oldList.get(0);
                compare(baseline, newList.get(i), path + "[" + i + "]", depth + 1);
            }
        } else if (oldValue instanceof Number) {
            if (!(newValue instanceof Number)) throw new IOException("Expected number: " + path);
            double n = ((Number) newValue).doubleValue();
            if (!Double.isFinite(n) || n < Integer.MIN_VALUE || n > Integer.MAX_VALUE ||
                    ((oldValue instanceof Integer || oldValue instanceof Long) && n != Math.rint(n)))
                throw new IOException("Invalid number: " + path);
            if (Arrays.asList("config.Claim.MaxSize", "config.Claim.MaxDepth", "config.Claim.MaxClaims").contains(path) && n < 0)
                throw new IOException("Claim limits must be non-negative.");
        } else if (oldValue instanceof Boolean) {
            if (!(newValue instanceof Boolean)) throw new IOException("Expected boolean: " + path);
        } else if (oldValue instanceof String) {
            if (!(newValue instanceof String)) throw new IOException("Expected text: " + path);
        } else if (!Objects.equals(oldValue, newValue)) throw new IOException("Unsupported setting: " + path);
    }

    private static void stringList(Object value, String path) throws IOException {
        if (!(value instanceof List) || ((List<?>) value).size() > 1000) throw new IOException("Expected text list: " + path);
        for (Object item : (List<?>) value)
            if (!(item instanceof String) || ((String) item).trim().isEmpty()) throw new IOException("Expected non-empty text: " + path);
    }

    @FunctionalInterface
    public interface Reload { void run() throws Exception; }

    public static Path apply(Path file, byte[] snapshot, String edited, Reload reload) throws Exception {
        validate(new String(snapshot, StandardCharsets.UTF_8), edited);
        Path backups = file.getParent().resolve("editor-backups");
        Files.createDirectories(backups);
        Path backup = backups.resolve("config-" + System.currentTimeMillis() + "-" + UUID.randomUUID() + ".yml");
        // Back up the current file, but intentionally allow intervening local changes.
        Files.write(backup, read(file), StandardOpenOption.CREATE_NEW);
        replace(file, edited.getBytes(StandardCharsets.UTF_8));
        try {
            reload.run();
            if (!equivalent(edited, new String(read(file), StandardCharsets.UTF_8)))
                throw new IOException("Saved configuration does not match the submission.");
        } catch (Exception failure) {
            throw new ReloadFailure(failure);
        }
        return backup;
    }

    public static final class ReloadFailure extends IOException {
        public ReloadFailure(Exception cause) { super("Configuration written, but reload failed. No rollback was performed.", cause); }
    }

    public static boolean equivalent(String expected, String actual) throws Exception {
        return tree(effective(expected)).equals(tree(effective(actual)));
    }

    private static YamlConfiguration effective(String text) throws Exception {
        YamlConfiguration config = parse(text);
        for (String flag : Arrays.asList("PVP", "TNT", "ALLOW_DOOR", "ALLOW_LEVER", "ALLOW_BUTTON", "ALLOW_BED", "ALLOW_REQUESTS", "ALLOW_ITEM_PICKUP"))
            if (!config.contains("DefaultFlags." + flag)) config.set("DefaultFlags." + flag, flag.equals("ALLOW_REQUESTS"));
        return config;
    }

    private static Object tree(Object value) {
        value = normalize(value);
        if (value instanceof Map) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) result.put(entry.getKey(), tree(entry.getValue()));
            return result;
        }
        if (value instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object item : (List<?>) value) result.add(tree(item));
            return result;
        }
        return value;
    }

    public static byte[] read(Path file) throws IOException {
        if (Files.size(file) > MAX_BYTES) throw new IOException("config.yml exceeds 128 KiB.");
        byte[] result = Files.readAllBytes(file);
        if (result.length > MAX_BYTES) throw new IOException("config.yml exceeds 128 KiB.");
        return result;
    }

    public static void replace(Path file, byte[] bytes) throws IOException {
        Path temp = Files.createTempFile(file.getParent(), ".editor-", ".tmp");
        try {
            Files.write(temp, bytes);
            // Fail closed if this filesystem cannot atomically replace the configuration.
            Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally { Files.deleteIfExists(temp); }
    }
}
