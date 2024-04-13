package gate.io;

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

	private PersistentSet(Class<T> type, Path path, Set<T> values, long log)
	{
		this.log = log;
		this.path = path;
		this.type = type;
		this.values = values;
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
			log += PersistentSet.persist(List.of(value), "+", path);
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
			log += PersistentSet.persist(elements, "+", path);
			values.addAll(elements);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public boolean remove(Object value)
	{
		if (type.isAssignableFrom(value.getClass()) && values.contains(value))
		{
			log += PersistentSet.persist(List.of(value), "-", path);
			values.remove(value);
			compact();
			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> collection)
	{
		var elements = collection.stream().filter(e -> type.isAssignableFrom(e.getClass()))
				.filter(e -> values.contains((T) e)).toList();

		if (!elements.isEmpty())
		{
			log += PersistentSet.persist(elements, "-", path);
			values.removeAll(elements);
			compact();
			return true;
		}

		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection)
	{
		var elements = values.stream().filter(e -> !collection.contains(e)).toList();

		if (!elements.isEmpty())
		{
			log += PersistentSet.persist(elements, "-", path);
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

				log += PersistentSet.persist(List.of(value), "-", path);
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
	public boolean contains(Object o)
	{
		return values.contains(o);
	}

	@Override
	public Object[] toArray()
	{
		return values.toArray();
	}

	@Override
	public <E> E[] toArray(E[] a)
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
					PersistentSet.persist(values, "+", backup);
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

	public static <T> PersistentSet<T> of(Class<T> type, Path path)
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
						JsonObject entry = JsonObject.parse(line);
						var value = entry.get("v").toObject(type);
						switch (entry.getString("action").orElseThrow())
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

			return new PersistentSet<>(type, path, values, log);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	private static long persist(Collection<?> values, String action, Path path)
	{
		try
		{
			if (!Files.exists(path))
				Files.createFile(path);

			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND))
			{
				for (Object value : values)
				{
					JsonObject line =
							new JsonObject().setString("a", action).set("v", JsonElement.of(value));
					writer.append(line.toString());
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
