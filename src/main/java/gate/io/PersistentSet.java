package gate.io;

import gate.annotation.SecurityAlgorithm;
import gate.annotation.SecurityKey;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PersistentSet<T> implements Set<T>
{

	private long log;
	private final Path path;
	private final Class<T> type;
	private final Set<T> values;
	private final Encryptor encryptor;

	private PersistentSet(Class<T> type, Path path, Set<T> values, long log, Encryptor encryptor)
	{
		this.log = log;
		this.path = path;
		this.type = type;
		this.values = values;
		this.encryptor = encryptor;
	}

	public Path getPath()
	{
		return path;
	}

	@Override
	public boolean add(T value)
	{
		if (!values.contains(value))
		{
			log += PersistentSet.persist(List.of(value), "+", path, encryptor);
			values.add(value);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> collection)
	{
		var elements = collection.stream().filter(e -> !values.contains(e)).toList();
		if (!elements.isEmpty())
		{
			log += PersistentSet.persist(elements, "+", path, encryptor);
			values.addAll(elements);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public boolean remove(Object value)
	{
		if (type.isAssignableFrom(value.getClass())
			&& values.contains((T) value))
		{
			log += PersistentSet.persist(List.of(value), "-", path, encryptor);
			values.remove(value);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public boolean removeAll(Collection<?> collection)
	{
		var elements = collection.stream()
			.filter(e -> type.isAssignableFrom(e.getClass()))
			.filter(e -> values.contains((T) e)).toList();

		if (!elements.isEmpty())
		{
			log += PersistentSet.persist(elements, "-", path, encryptor);
			values.removeAll(elements);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection)
	{
		var elements = values.stream()
			.filter(e -> !collection.contains(e)).toList();

		if (!elements.isEmpty())
		{
			log += PersistentSet.persist(elements, "-", path, encryptor);
			values.removeAll(elements);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public void clear()
	{
		try
		{
			Files.deleteIfExists(path);
			log = 0;
			values.clear();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public Iterator<T> iterator()
	{
		var iterator = values.iterator();
		return new Iterator<T>()
		{

			private T value;

			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public T next()
			{
				return value = iterator.next();
			}

			@Override
			public void remove()
			{

				log += PersistentSet.persist(List.of(value), "-", path, encryptor);
				iterator.remove();
				compact();
			}

			@Override
			public void forEachRemaining(Consumer<? super T> action)
			{
				iterator.forEachRemaining(action);
			}
		};
	}

	@Override
	public int size()
	{
		return values.size();

	}

	@Override
	public boolean isEmpty()
	{
		return values.isEmpty();
	}

	@Override
	public boolean contains(Object o
	)
	{
		return values.contains(o);
	}

	@Override
	public Object[] toArray()
	{
		return values.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a
	)
	{
		return values.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return values.containsAll(c);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof PersistentSet && values.equals(o);
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	private void compact()
	{
		try
		{
			if (log >= values.size() * 2)
			{
				Path backup = path.resolveSibling(path.getFileName() + ".backup");
				Files.deleteIfExists(backup);

				if (!values.isEmpty())
				{
					PersistentSet.persist(values, "+", backup, encryptor);
					Files.move(backup, path, StandardCopyOption.REPLACE_EXISTING);
				} else
					Files.deleteIfExists(path);
			}

			log = values.size();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public static <T> PersistentSet<T> of(Class<T> type, Path path, Encryptor encryptor)
	{
		try
		{
			int log = 0;
			var values = new HashSet<T>();

			if (Files.exists(path))
			{
				try (BufferedReader reader = Files.newBufferedReader(path))
				{
					for (String line = reader.readLine(); line != null; line = reader.readLine())
					{
						log++;

						if (encryptor != null)
							line = encryptor.decrypt(line);

						JsonObject entry = JsonObject.parse(line);
						var value = entry.get("v").toObject(type);
						switch (entry.getString("a").orElseThrow())
						{
							case "+":
								values.add(value);
								break;
							case "-":
								values.remove(value);
								break;
							default:
								throw new IOException("File is corrupted");
						}
					}
				}
			}

			return new PersistentSet<>(type, path, values, log, encryptor);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public static <T> PersistentSet<T> of(Class<T> type, Path path)
	{
		String algorithm = type.isAnnotationPresent(SecurityAlgorithm.class)
			? type.getAnnotation(SecurityKey.class).value() : "AES";
		String key = type.isAnnotationPresent(SecurityKey.class)
			? type.getAnnotation(SecurityKey.class).value() : null;
		Encryptor encryptor = key != null ? Encryptor.of(algorithm, key) : null;
		return of(type, path, encryptor);
	}

	private static long persist(Collection<?> values, String action, Path path, Encryptor encryptor)
	{
		try
		{
			if (!Files.exists(path))
			{
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}

			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND))
			{
				for (Object value : values)
				{
					String line = new JsonObject()
						.setString("a", action)
						.set("v", JsonElement.of(value))
						.toString();

					if (encryptor != null)
						line = encryptor.encrypt(line);

					writer.append(line);
					writer.newLine();
				}
			}

			return values.size();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
