package me.RonanCraft.BetterClaims.web.editor;

import org.junit.Test;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

public class EditorConfigTest {
    private String config() throws Exception {
        // The test API is 1.8; the receiving server checks its own material names.
        return new String(Files.readAllBytes(Paths.get("../../src/main/resources/config.yml")), StandardCharsets.UTF_8).replace("golden_shovel", "GOLD_SPADE");
    }
    @Test public void validatesAndRejectsUnsafeChanges() throws Exception {
        String original = config();
        SchemaConfig.validate(original, original.replace("MaxClaims: 3", "MaxClaims: 7"));
        SchemaConfig.validate(original, original.replace("MaxClaims: 3", "MaxClaims: -1"));
        for (String invalid : new String[] {
            original.replace("MaxClaims: 3", "MaxClaims: wrong"),
            original + "\nBad: !!java/object {}\n"
        }) {
            try { SchemaConfig.validate(original, invalid); fail("Accepted invalid configuration"); }
            catch (Exception expected) { }
        }
    }
    @Test public void backsUpAndAcceptsRuntimeFlagDefaults() throws Exception {
        String original = config(), edited = original.replace("MaxClaims: 3", "MaxClaims: 7");
        Path file = Files.createTempDirectory("claims-editor-test").resolve("config.yml");
        Files.write(file, original.getBytes(StandardCharsets.UTF_8));
        Path backup = SchemaConfig.apply(file, original.getBytes(StandardCharsets.UTF_8), edited, () -> {
            String loaded = edited.replace("DefaultFlags: {}", "DefaultFlags: {PVP: false, ALLOW_REQUESTS: true}");
            Files.write(file, loaded.getBytes(StandardCharsets.UTF_8));
        });
        assertEquals(original, new String(Files.readAllBytes(backup), StandardCharsets.UTF_8));
        assertTrue(SchemaConfig.equivalent(edited, new String(Files.readAllBytes(file), StandardCharsets.UTF_8)));
        try {
            SchemaConfig.apply(file, edited.getBytes(StandardCharsets.UTF_8), edited, () -> { throw new Exception("reload failed"); });
            fail("Expected reload failure");
        } catch (com.ronanplugins.bettersync.editor.EditorFiles.ReloadFailure expected) { }
        assertEquals(edited, new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
    }
}
