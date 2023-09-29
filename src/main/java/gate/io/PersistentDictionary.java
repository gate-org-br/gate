package gate.io;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A persistent dictionary for storing key-value pairs of data in a file.
 *
 * This class provides a way to persistently store and manage key-value data in a file.
 *
 * @param <T> The type of values stored in the dictionary.
 */
public class PersistentDictionary<T>
{

	private long log;
	private final Path path;
	private final ConcurrentMap<String, T> values;

	private PersistentDictionary(ConcurrentMap<String, T> values, Path path, long log)
	{
		this.log = log;
		this.path = path;
		this.values = values;
	}

	/**
	 * Creates an instance of PersistentDictionary with the specified data type and file path.
	 *
	 * @param type The class type of the values to be stored in the dictionary.
	 * @param path The path to the file where the dictionary's data is stored.
	 * @return A PersistentDictionary instance initialized with the data from the file, or a new one if the file does not exist.
	 * @throws UncheckedIOException If an I/O error occurs while reading the data from the file.
	 */
	public static <T> PersistentDictionary<T> of(Class<T> type, Path path)
	{
		try
		{
			int log = 0;
			var values = new ConcurrentHashMap<String, T>();
			if (Files.exists(path))
			{
				try (BufferedReader reader = Files.newBufferedReader(path))
				{
					for (String line = reader.readLine(); line != null; line = reader.readLine())
					{
						JsonObject entry = JsonObject.parse(line);
						String key = entry.getString("key").orElseThrow();
						JsonElement value = entry.get("value");
						if (value == null || value instanceof JsonNull)
							values.remove(key);
						else
							values.put(key, value.toObject(type));
						log++;
					}
				}
			}

			return new PersistentDictionary<>(values, path, log);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Returns the number of key-value pairs in the persistent dictionary.
	 *
	 * @return The size of the dictionary, indicating the number of entries.
	 */
	public int size()
	{
		return values.size();
	}

	/**
	 * Checks if the persistent dictionary is empty.
	 *
	 * @return true if the dictionary contains no entries, false otherwise.
	 */
	public boolean isEmpty()
	{
		return values.isEmpty();
	}

	/**
	 * Checks if the persistent dictionary contains a specific key.
	 *
	 * @param key The key to be checked for presence in the dictionary.
	 * @return true if the key exists in the dictionary, false otherwise.
	 */
	public boolean containsKey(String key)
	{
		return values.containsKey(key);
	}

	/**
	 * Checks if the persistent dictionary contains a specific key.
	 *
	 * @param key The key to be checked for presence in the dictionary.
	 * @return true if the key exists in the dictionary, false otherwise.
	 */
	public T get(String key)
	{
		return values.get(key);
	}

	/**
	 * Adds or updates a value associated with a key in the persistent dictionary.
	 *
	 * @param key The key by which the value will be associated.
	 * @param value The value to be stored in the dictionary.
	 * @return The previous value associated with the key, or null if the key did not exist previously.
	 * @throws UncheckedIOException If an I/O error occurs while writing data to the file.
	 */
	public synchronized T put(String key, T value)
	{

		try
		{
			if (!Files.exists(path))
				Files.createFile(path);
			JsonObject entry = new JsonObject()
				.setString("key", key)
				.set("value", JsonElement.of(value));
			Files.writeString(path, entry + System.lineSeparator(), StandardOpenOption.APPEND);

			var result = values.put(key, value);

			if (log > 0 && values.size() * 100 / log < 50)
				compact();

			log++;
			return result;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Removes a value associated with the specified key from the persistent dictionary.
	 *
	 * @param key The key for which the associated value will be removed.
	 * @return The value previously associated with the key, or null if the key was not found.
	 * @throws UncheckedIOException If an I/O error occurs while updating the data in the file.
	 */
	public synchronized T remove(String key)
	{
		try
		{
			JsonObject entry = new JsonObject()
				.setString("key", key)
				.set("value", JsonNull.INSTANCE);
			Files.writeString(path, entry + System.lineSeparator(), StandardOpenOption.APPEND);

			var result = values.remove(key);

			if (log > 0 && values.size() * 100 / log < 50)
				compact();

			log++;
			return result;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Clears all entries in the persistent dictionary, removing all data and the associated file.
	 *
	 * This method deletes all key-value pairs in the dictionary and removes the data file from storage.
	 *
	 * @throws UncheckedIOException If an I/O error occurs during the clearing process.
	 */
	public synchronized void clear()
	{
		try
		{
			Files.delete(path);
			values.clear();
			log = 0;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Compacts the persistent dictionary by optimizing file storage and removing obsolete entries.
	 *
	 * This method creates a backup file, copies valid data to it, and replaces the original file with the compacted data.
	 *
	 * @throws UncheckedIOException If an I/O error occurs during the compaction process.
	 */
	public synchronized void compact()
	{
		try
		{
			Path backup = path.resolveSibling(path.getFileName() + ".backup");
			if (Files.deleteIfExists(backup));

			if (!values.isEmpty())
			{
				Files.createFile(backup);
				try (BufferedWriter writer = Files.newBufferedWriter(backup, StandardOpenOption.APPEND))
				{
					for (var entry : values.entrySet())
					{
						JsonObject value = new JsonObject()
							.setString("key", entry.getKey())
							.set("value", JsonElement.of(entry.getValue()));
						writer.append(value.toString());
						writer.newLine();
					}
				}

				Files.move(backup, path, StandardCopyOption.REPLACE_EXISTING);
			} else if (Files.deleteIfExists(path));

			log = values.size();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
