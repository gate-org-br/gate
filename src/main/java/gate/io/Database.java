package gate.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Database<T> implements Observable<T>
{

	protected final Path folder;
	protected final Class<T> type;
	protected final Map<String, Set<T>> tables;
	protected final List<Observer<T>> observers = new CopyOnWriteArrayList<>();

	Database(Class<T> type, Path folder, Map<String, Set<T>> tables)
	{
		this.type = type;
		this.folder = folder;
		this.tables = tables;
	}

	public boolean isEmpty()
	{
		return tables.values().stream().allMatch(e -> e.isEmpty());
	}

	public int size()
	{
		return tables.values().stream().mapToInt(Set::size).sum();
	}

	public boolean isEmpty(String tableName)
	{
		return !tables.containsKey(tableName) || tables.get(tableName).isEmpty();
	}

	public int size(String tableName)
	{
		return tables.containsKey(tableName) ? tables.get(tableName).size() : 0;
	}

	public Optional<T> select()
	{
		return tables.values().stream().flatMap(Set::stream).findAny();
	}

	public Optional<T> select(Predicate<T> predicate)
	{
		return tables.values().stream().flatMap(Set::stream).filter(predicate).findAny();
	}

	public Optional<T> select(Comparator<T> comparator)
	{
		return tables.values().stream().flatMap(Set::stream).min(comparator);
	}

	public Optional<T> select(Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream().flatMap(Set::stream).filter(predicate).min(comparator);
	}

	public List<T> search()
	{
		return tables.values().stream().flatMap(Set::stream).collect(Collectors.toList());
	}

	public List<T> search(Predicate<T> predicate)
	{
		return tables.values().stream().flatMap(Set::stream).filter(predicate)
				.collect(Collectors.toList());
	}

	public List<T> search(Comparator<T> comparator)
	{
		return tables.values().stream().flatMap(Set::stream).sorted(comparator)
				.collect(Collectors.toList());
	}

	public List<T> search(Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream().flatMap(Set::stream).filter(predicate).sorted(comparator)
				.collect(Collectors.toList());
	}

	public Optional<T> select(String tableName)
	{
		return tables.entrySet().stream().filter(e -> e.getKey().equals(tableName)).findAny()
				.flatMap(e -> e.getValue().stream().findAny());
	}

	public Optional<T> select(String tableName, Predicate<T> predicate)
	{
		return tables.entrySet().stream().filter(e -> e.getKey().equals(tableName)).findAny()
				.flatMap(e -> e.getValue().stream().filter(predicate).findAny());
	}

	public Optional<T> select(String tableName, Comparator<T> comparator)
	{
		return tables.entrySet().stream().filter(e -> e.getKey().equals(tableName)).findAny()
				.flatMap(e -> e.getValue().stream().min(comparator));
	}

	public Optional<T> select(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.entrySet().stream().filter(e -> e.getKey().equals(tableName)).findAny()
				.flatMap(e -> e.getValue().stream().filter(predicate).sorted(comparator).findAny());
	}

	public List<T> search(String tableName)
	{
		return tables.containsKey(tableName) ? new ArrayList<>(tables.get(tableName))
				: Collections.emptyList();
	}

	public List<T> search(String tableName, Predicate<T> predicate)
	{
		return tables.containsKey(tableName)
				? tables.get(tableName).stream().filter(predicate).collect(Collectors.toList())
				: Collections.emptyList();
	}

	public List<T> search(String tableName, Comparator<T> comparator)
	{
		return tables.containsKey(tableName)
				? tables.get(tableName).stream().sorted(comparator).collect(Collectors.toList())
				: Collections.emptyList();
	}

	public List<T> search(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.containsKey(tableName) ? tables.get(tableName).stream().filter(predicate)
				.sorted(comparator).collect(Collectors.toList()) : Collections.emptyList();
	}

	public long count(String tableName, Predicate<T> predicate)
	{
		return tables.entrySet().stream().filter(e -> e.getKey().equals(tableName)).findAny()
				.map(e -> e.getValue().stream().filter(predicate).count()).orElse(Long.valueOf(0));
	}

	public void delete(String tableName, Predicate<T> predicate)
	{
		Set<T> table = tables.get(tableName);
		table.removeIf(predicate);
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	public void delete(Predicate<T> predicate)
	{
		tables.values().forEach(e -> e.removeIf(predicate));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	public void delete(Collection<T> values)
	{
		tables.values().forEach(e -> e.removeAll(values));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	public void delete(List<T> values)
	{
		tables.values().forEach(e -> e.removeAll(values));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	public void delete(String tableName, Collection<T> values)
	{
		Set<T> table = tables.get(tableName);
		table.removeAll(values);
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	@SafeVarargs
	public final void delete(String tableName, T... values)
	{
		Set<T> table = tables.get(tableName);
		table.removeAll(Arrays.asList(values));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	public void insert(String tableName, Collection<T> values)
	{
		Set<T> table = tables.computeIfAbsent(tableName,
				e -> Collections.synchronizedSet(PersistentSet.of(type, folder.resolve(e))));
		table.addAll(values);
		observers.forEach(Observer::onUpdate);
	}

	@SafeVarargs
	public final void insert(String table, T... values)
	{
		insert(table, Arrays.asList(values));
	}

	public void drop(String tableName)
	{
		if (tables.containsKey(tableName))
		{
			Set<T> table = tables.remove(tableName);
			table.clear();
			observers.forEach(Observer::onUpdate);
		}
	}

	public void drop()
	{
		try
		{
			tables.values().forEach(Set::clear);
			tables.clear();
			Files.deleteIfExists(folder);
			observers.forEach(Observer::onUpdate);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public static <T> Database<T> of(Class<T> type, Path folder)
	{
		try
		{
			if (Files.notExists(folder))
				Files.createDirectory(folder);

			Map<String, Set<T>> tables = new HashMap<>();
			Files.list(folder).forEach(path -> tables.put(path.getFileName().toString(),
					PersistentSet.of(type, path)));
			return new Database<>(type, folder, tables);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void addObserver(Observer<T> observer)
	{
		observers.add(observer);
	}

	@Override
	public void remObserver(Observer<T> observer)
	{
		observers.remove(observer);
	}
}
