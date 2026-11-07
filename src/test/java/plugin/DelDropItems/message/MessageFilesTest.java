package plugin.DelDropItems.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

public class MessageFilesTest {
    private static final String[] LANGUAGES = {"en", "ru", "fr", "de", "es", "it", "cn", "ja"};

    private static YamlConfiguration load(String language) throws Exception {
        InputStream in = MessageFilesTest.class.getResourceAsStream("/messages_" + language + ".yml");
        assertNotNull("missing messages_" + language + ".yml", in);
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        }
    }

    @Test
    public void everyDocumentedLanguageHasTheSameKeysAsEnglish() throws Exception {
        Set<String> english = load("en").getKeys(false);
        for (String language : LANGUAGES) {
            assertEquals("keys of messages_" + language + ".yml", english, load(language).getKeys(false));
        }
    }

    @Test
    public void fillReplacesAllPairs() {
        assertEquals("a=1 b=2 a=1",
                Messages.fill("a=%a% b=%b% a=%a%", "%a%", "1", "%b%", "2"));
    }

    @Test
    public void fillIgnoresDanglingKey() {
        assertEquals("x=%x%", Messages.fill("x=%x%", "%x%"));
    }
}
