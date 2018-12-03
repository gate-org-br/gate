package gate.io;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractTable<T> implements Table<T>
{

	protected final File file;
	protected final Set<T> values;
	protected final Class<T> type;
	protected static final String ALGORITHM = "AES";
	protected final List<Observer<T>> observers = new CopyOnWriteArrayList<>();

	protected AbstractTable(Class<T> type, File file, Set<T> values)
	{
		this.file = file;
		this.type = type;
		this.values = values;
	}

	@Override
	public void insert(Collection<T> values)
	{
		this.values.removeAll(values);
		this.values.addAll(values);
		persist();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public File getFile()
	{
		return file;
	}

	@Override
	@SafeVarargs
	public final void insert(T... values)
	{
		insert(Arrays.asList(values));
	}

	@Override
	public void delete(Predicate<T> predicate)
	{
		values.removeIf(predicate);
		persist();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public void delete(Collection<T> values)
	{
		this.values.removeAll(values);
		persist();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	@SafeVarargs
	public final void delete(T... values)
	{
		this.values.removeAll(Arrays.asList(values));
		persist();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public boolean isEmpty()
	{
		return this.values.isEmpty();
	}

	@Override
	public int size()
	{
		return this.values.size();
	}

	@Override
	public Optional<T> select()
	{
		return values.stream()
				.findAny();
	}

	@Override
	public Optional<T> select(Comparator<T> comparator)
	{
		return values.stream()
				.sorted(comparator)
				.findFirst();
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

	@Override
	public Optional<T> select(Predicate<T> predicate)
	{
		return values.stream()
				.filter(predicate)
				.findAny();
	}

	@Override
	public Optional<T> select(Predicate<T> predicate, Comparator<T> comparator)
	{
		return values.stream()
				.filter(predicate)
				.sorted(comparator)
				.findFirst();
	}

	@Override
	public List<T> search()
	{
		return values.stream()
				.collect(Collectors.toList());
	}

	@Override
	public List<T> search(Comparator<T> comparator)
	{
		return values.stream()
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	@Override
	public List<T> search(Predicate<T> predicate)
	{
		return values.stream()
				.filter(predicate)
				.collect(Collectors.toList());
	}

	@Override
	public List<T> search(Predicate<T> predicate, Comparator<T> comparator)
	{
		return values.stream()
				.filter(predicate)
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	@Override
	public void drop()
	{
		values.clear();
		persist();
		observers.forEach(Observer::onUpdate);
	}

	@Override
	public long count(Predicate<T> predicate)
	{
		return values.stream().filter(predicate).count();
	}

	protected abstract void persist();
}
