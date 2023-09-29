package gate.io;

import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PersistentDictionaryTest
{

	@TempDir
	Path tempDir;

	private PersistentDictionary<String> dictionary;

	@BeforeEach
	public void setup()
	{
		// Create a new dictionary for each test
		dictionary = PersistentDictionary.of(String.class, tempDir.resolve("test-dictionary.json"));
	}

	@Test
	public void putAndGet()
	{
		dictionary.put("key1", "value1");
		assertEquals("value1", dictionary.get("key1"));
	}

	@Test
	public void putOverwrite()
	{
		dictionary.put("key2", "value2");
		dictionary.put("key2", "new-value");
		assertEquals("new-value", dictionary.get("key2"));
	}

	@Test
	public void remove()
	{
		dictionary.put("key3", "value3");
		String removedValue = dictionary.remove("key3");
		assertNull(dictionary.get("key3"));
		assertEquals("value3", removedValue);
	}

	@Test
	public void size()
	{
		assertEquals(0, dictionary.size());
		dictionary.put("key4", "value4");
		assertEquals(1, dictionary.size());
	}

	@Test
	public void isEmpty()
	{
		assertTrue(dictionary.isEmpty());
		dictionary.put("key5", "value5");
		assertFalse(dictionary.isEmpty());
	}

	@Test
	public void containsKey()
	{
		dictionary.put("key6", "value6");
		assertTrue(dictionary.containsKey("key6"));
		assertFalse(dictionary.containsKey("non-existent-key"));
	}

	@Test
	public void clear()
	{
		dictionary.put("key7", "value7");
		assertFalse(dictionary.isEmpty());
		dictionary.clear();
		assertTrue(dictionary.isEmpty());
		assertNull(dictionary.get("key7"));
	}

	@Test
	public void compact()
	{
		dictionary.put("key8", "value8");
		dictionary.remove("key8");
		dictionary.compact();
		assertNull(dictionary.get("key8"));
	}
}
