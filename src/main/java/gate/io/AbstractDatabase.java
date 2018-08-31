package gate.io;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

abstract class AbstractDatabase<T> implements Database<T>
{

	protected final File folder;
	protected final Class<T> type;
	protected final Map<String, Table<T>> tables;
	protected final List<Observer<T>> observers
			= new CopyOnWriteArrayList<>();

	AbstractDatabase(Class<T> type, File folder, Map<String, Table<T>> tables)
	{
		this.type = type;
		this.folder = folder;
		this.tables = tables;
	}

	@Override
	public boolean isEmpty()
	{
		return tables.isEmpty();
	}

	@Override
	public int size()
	{
		return tables.values().stream()
				.mapToInt(e -> e.size()).sum();
	}

	@Override
	public boolean isEmpty(String tableName)
	{
		return tables.containsKey(tableName)
				? tables.get(tableName).isEmpty() : true;
	}

	@Override
	public int size(String tableName)
	{
		return tables.containsKey(tableName)
				? tables.get(tableName).size() : 0;
	}

	@Override
	public Optional<T> select()
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.findAny();
	}

	@Override
	public Optional<T> select(Predicate<T> predicate)
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.filter(predicate)
				.findAny();
	}

	@Override
	public Optional<T> select(Comparator<T> comparator)
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.sorted(comparator)
				.findFirst();
	}

	@Override
	public Optional<T> select(Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.filter(predicate)
				.sorted(comparator)
				.findFirst();
	}

	@Override
	public List<T> search()
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.collect(Collectors.toList());
	}

	@Override
	public List<T> search(Predicate<T> predicate)
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.filter(predicate)
				.collect(Collectors.toList());
	}

	@Override
	public List<T> search(Comparator<T> comparator)
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	@Override
	public List<T> search(Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream()
				.flatMap(e -> e.search().stream())
				.filter(predicate)
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<T> select(String tableName)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().flatMap(e -> e.select());
	}

	@Override
	public Optional<T> select(String tableName, Predicate<T> predicate)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().flatMap(e -> e.select(predicate));
	}

	@Override
	public Optional<T> select(String tableName, Comparator<T> comparator)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().flatMap(e -> e.select(comparator));
	}

	@Override
	public Optional<T> select(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().flatMap(e -> e.select(predicate, comparator));
	}

	@Override
	public List<T> search(String tableName)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().map(e -> e.search()).orElse(Collections.emptyList());
	}

	@Override
	public List<T> search(String tableName, Predicate<T> predicate)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().map(e -> e.search(predicate)).orElse(Collections.emptyList());
	}

	@Override
	public List<T> search(String tableName, Comparator<T> comparator)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().map(e -> e.search(comparator)).orElse(Collections.emptyList());
	}

	@Override
	public List<T> search(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
				.findAny().map(e -> e.search(predicate, comparator)).orElse(Collections.emptyList());
	}

	@Override
	public long count(String tableName, Predicate<T> predicate)
	{
		return tables
				.values()
				.stream()
				.filter(e -> e.getFile().getName().equals(tableName))
				.findAny().map(e -> e.count(predicate))
				.orElse(Long.valueOf(0));
	}

	@Override
	public void delete(String tableName,
			Predicate<T> predicate)
	{
		Table<T> table = tables.get(tableName);
		table.delete(predicate);
		if (table.isEmpty())
			table.drop();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public void delete(Predicate<T> predicate)
	{
		tables.values().stream()
				.forEach(e -> e.delete(predicate));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public void delete(Collection<T> values)
	{
		tables.values().stream()
				.forEach(e -> e.delete(values));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	@Override
	@SafeVarargs
	public final void delete(T... values)
	{
		delete(Arrays.asList(values));
	}

	@Override
	public void delete(List<T> values)
	{
		tables.values().stream()
				.forEach(e -> e.delete(values));
		tables.values().removeIf(e -> e.isEmpty());
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public void delete(String tableName, Collection<T> values)
	{
		Table<T> table = tables.get(tableName);
		table.delete(values);
		if (table.isEmpty())
			table.drop();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	@SafeVarargs
	public final void delete(String tableName, T... values)
	{
		Table<T> table = tables.get(tableName);
		table.delete(values);
		if (table.isEmpty())
			table.drop();
		observers.forEach(Observer::onUpdate);
	}

	public abstract Table<T> create(String fileName);

	@Override
	public void insert(String table, Collection<T> values)
	{
		tables.computeIfAbsent(table,
				e -> create(e))
				.insert(values);
		observers.forEach(Observer::onUpdate);
	}

	@Override
	@SafeVarargs
	public final void insert(String table, T... values)
	{
		tables.computeIfAbsent(table,
				e -> create(e))
				.insert(values);
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public void drop(String tableName)
	{
		if (tables.containsKey(tableName))
		{
			tables.remove(tableName).drop();
			observers.forEach(Observer::onUpdate);
		}
	}

	@Override
	public void drop()
	{
		tables.values().forEach(table -> table.drop());
		folder.delete();
		observers.forEach(Observer::onUpdate);
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
