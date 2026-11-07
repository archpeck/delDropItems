package plugin.DelDropItems.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

public class SettingsTest {

    private static Settings load(String yaml) throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);
        Settings settings = new Settings();
        settings.load(config);
        return settings;
    }

    @Test
    public void emptyConfigUsesDocumentedDefaults() throws Exception {
        Settings s = load("");
        assertEquals(300L, s.removalTime());
        assertEquals(300L, s.broadcastInterval());
        assertEquals(0D, s.distancePlayerToObject(), 0D);
        assertTrue(s.onlyPlayerDropped());
        assertFalse(s.ignoreNamedItems());
        assertTrue(s.logRemovals());
        assertTrue(s.dropMessageAllow());
        assertTrue(s.reloadMessageAllow());
        assertTrue(s.broadcastMessageAllow());
        assertEquals("en", s.language());
        assertTrue(s.ignoredMaterials().isEmpty());
    }

    @Test
    public void bundledConfigMatchesCodeDefaults() throws Exception {
        YamlConfiguration config;
        try (Reader reader = new InputStreamReader(
                SettingsTest.class.getResourceAsStream("/config.yml"), StandardCharsets.UTF_8)) {
            config = YamlConfiguration.loadConfiguration(reader);
        }
        Settings fromFile = new Settings();
        fromFile.load(config);
        Settings defaults = load("");

        assertEquals(defaults.removalTime(), fromFile.removalTime());
        assertEquals(defaults.broadcastInterval(), fromFile.broadcastInterval());
        assertEquals(defaults.distancePlayerToObject(), fromFile.distancePlayerToObject(), 0D);
        assertEquals(defaults.onlyPlayerDropped(), fromFile.onlyPlayerDropped());
        assertEquals(defaults.ignoreNamedItems(), fromFile.ignoreNamedItems());
        assertEquals(defaults.logRemovals(), fromFile.logRemovals());
        assertEquals(defaults.dropMessageAllow(), fromFile.dropMessageAllow());
        assertEquals(defaults.reloadMessageAllow(), fromFile.reloadMessageAllow());
        assertEquals(defaults.broadcastMessageAllow(), fromFile.broadcastMessageAllow());
        assertEquals(defaults.language(), fromFile.language());
    }

    @Test
    public void removalTimeIsAtLeastOneSecond() throws Exception {
        assertEquals(1L, load("item-removal-time: 0").removalTime());
        assertEquals(1L, load("item-removal-time: -5").removalTime());
    }

    @Test
    public void ignoredMaterialsAreCaseInsensitiveAndUnknownNamesAreReported() throws Exception {
        Settings s = load("ignored-materials: [DIAMOND, diamond_block, NOT_A_MATERIAL]");
        assertTrue(s.ignoredMaterials().contains(Material.DIAMOND));
        assertTrue(s.ignoredMaterials().contains(Material.DIAMOND_BLOCK));
        assertEquals(1, s.unknownMaterials().size());
        assertEquals("NOT_A_MATERIAL", s.unknownMaterials().get(0));
    }

    @Test
    public void disabledWorldsAreCaseSensitive() throws Exception {
        Settings s = load("disabled-worlds: [world_nether]");
        assertTrue(s.isWorldDisabled("world_nether"));
        assertFalse(s.isWorldDisabled("World_Nether"));
        assertFalse(s.isWorldDisabled("world"));
    }
}
