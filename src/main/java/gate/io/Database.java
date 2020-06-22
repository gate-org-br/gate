package gate.io;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Database<T> implements Observable<T>
{

	protected final File folder;
	protected final Class<T> type;
	protected final Map<String, PersistentSet<T>> tables;
	protected final List<Observer<T>> observers
		= new CopyOnWriteArrayList<>();

	Database(Class<T> type, File folder, Map<String, PersistentSet<T>> tables)
	{
		this.type = type;
		this.folder = folder;
		this.tables = tables;
	}

	public boolean isEmpty()
	{
		return tables.values()
			.stream().allMatch(AbstractCollection::isEmpty);
	}

	public int size()
	{
		return tables.values().stream()
			.mapToInt(PersistentSet::size).sum();
	}

	public boolean isEmpty(String tableName)
	{
		return !tables.containsKey(tableName) || tables.get(tableName).isEmpty();
	}

	public int size(String tableName)
	{
		return tables.containsKey(tableName)
			? tables.get(tableName).size() : 0;
	}

	public Optional<T> select()
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.findAny();
	}

	public Optional<T> select(Predicate<T> predicate)
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.filter(predicate)
			.findAny();
	}

	public Optional<T> select(Comparator<T> comparator)
	{
		return tables.values().stream()
			.flatMap(Collection::stream).min(comparator);
	}

	public Optional<T> select(Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.filter(predicate).min(comparator);
	}

	public List<T> search()
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}

	public List<T> search(Predicate<T> predicate)
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.filter(predicate)
			.collect(Collectors.toList());
	}

	public List<T> search(Comparator<T> comparator)
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	public List<T> search(Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream()
			.flatMap(Collection::stream)
			.filter(predicate)
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	public Optional<T> select(String tableName)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
			.findAny().flatMap(e -> e.stream().findAny());
	}

	public Optional<T> select(String tableName, Predicate<T> predicate)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
			.findAny().flatMap(e -> e.stream().filter(predicate).findAny());
	}

	public Optional<T> select(String tableName, Comparator<T> comparator)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
			.findAny().flatMap(e -> e.stream().min(comparator));
	}

	public Optional<T> select(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return tables.values().stream().filter(e -> e.getFile().getName().equals(tableName))
			.findAny().flatMap(e -> e.stream().filter(predicate).sorted(comparator).findAny());
	}

	public List<T> search(String tableName)
	{
		return tables.containsKey(tableName)
			? new ArrayList<>(tables.get(tableName)) : Collections.emptyList();
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
		return tables.containsKey(tableName)
			? tables.get(tableName).stream().filter(predicate)
				.sorted(comparator).collect(Collectors.toList())
			: Collections.emptyList();
	}

	public long count(String tableName, Predicate<T> predicate)
	{
		return tables
			.values()
			.stream()
			.filter(e -> e.getFile().getName().equals(tableName))
			.findAny().map(e -> e.stream().filter(predicate).count())
			.orElse(Long.valueOf(0));
	}

	public void delete(String tableName, Predicate<T> predicate)
	{
		PersistentSet<T> table = tables.get(tableName);
		table.removeIf(predicate);
		table.commit();
		tables.values().removeIf(AbstractCollection::isEmpty);
		observers.forEach(Observer::onUpdate);
	}

	public void delete(Predicate<T> predicate)
	{
		tables.values().forEach(e -> e.removeIf(predicate));
		tables.values().forEach(PersistentSet::commit);
		tables.values().removeIf(AbstractCollection::isEmpty);
		observers.forEach(Observer::onUpdate);
	}

	public void delete(Collection<T> values)
	{
		tables.values().forEach(e -> e.removeAll(values));
		tables.values().forEach(PersistentSet::commit);
		tables.values().removeIf(AbstractCollection::isEmpty);
		observers.forEach(Observer::onUpdate);
	}

	@SafeVarargs
	public final void delete(T... values)
	{
		delete(Arrays.asList(values));
	}

	public void delete(List<T> values)
	{
		tables.values().forEach(e -> e.removeAll(values));
		tables.values().forEach(PersistentSet::commit);
		tables.values().removeIf(AbstractCollection::isEmpty);
		observers.forEach(Observer::onUpdate);
	}

	public void delete(String tableName, Collection<T> values)
	{
		PersistentSet<T> table = tables.get(tableName);
		table.removeAll(values);
		table.commit();
		tables.values().removeIf(AbstractCollection::isEmpty);
		observers.forEach(Observer::onUpdate);
	}

	@SafeVarargs
	public final void delete(String tableName, T... values)
	{
		PersistentSet<T> table = tables.get(tableName);
		table.removeAll(Arrays.asList(values));
		table.commit();
		tables.values().removeIf(AbstractCollection::isEmpty);
		observers.forEach(Observer::onUpdate);
	}

	public void insert(String tableName, Collection<T> values)
	{
		PersistentSet<T> table = tables.computeIfAbsent(tableName, e -> PersistentSet.of(type, new File(folder, e)));
		table.addAll(values);
		table.commit();
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
			PersistentSet<T> table = tables.remove(tableName);
			table.clear();
			table.commit();
			observers.forEach(Observer::onUpdate);
		}
	}

	public void drop()
	{
		tables.values().forEach(AbstractCollection::clear);
		tables.values().forEach(PersistentSet::commit);
		tables.clear();
		folder.delete();
		observers.forEach(Observer::onUpdate);
	}

	public static <T> Database<T> of(Class<T> type, File folder)
	{
		Map<String, PersistentSet<T>> tables = new HashMap<>();
		File[] files = folder.listFiles();
		if (files != null)
			Stream.of(files).forEach(e -> tables.put(e.getName(),
				PersistentSet.of(type, e)));
		return new Database<>(type, folder, tables);
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
