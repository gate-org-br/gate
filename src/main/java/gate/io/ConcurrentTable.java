package gate.io;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ConcurrentTable<T extends Serializable> implements Table<T>
{

	private final Table table;

	ConcurrentTable(Table table)
	{
		this.table = table;
	}

	@Override
	public synchronized void delete(Predicate<T> predicate)
	{
		table.delete(predicate);
	}

	@Override
	public synchronized void delete(Collection<T> values)
	{
		table.delete(values);
	}

	@Override
	public synchronized void delete(T... values)
	{
		table.delete(Arrays.asList(values));
	}

	@Override
	public synchronized void drop()
	{
		table.drop();
	}

	@Override
	public synchronized File getFile()
	{
		return table.getFile();
	}

	@Override
	public synchronized void insert(Collection<T> values)
	{
		table.insert(values);
	}

	@Override
	public synchronized void insert(T... values)
	{
		table.insert(Arrays.asList(values));
	}

	@Override
	public synchronized boolean isEmpty()
	{
		return table.isEmpty();
	}

	@Override
	public synchronized List<T> search()
	{
		return table.search();
	}

	@Override
	public synchronized List<T> search(Comparator<T> comparator)
	{
		return table.search(comparator);
	}

	@Override
	public synchronized List<T> search(Predicate<T> predicate)
	{
		return table.search(predicate);
	}

	@Override
	public synchronized List<T> search(Predicate<T> predicate, Comparator<T> comparator)
	{
		return table.search(predicate, comparator);
	}

	@Override
	public synchronized Optional<T> select()
	{
		return table.select();
	}

	@Override
	public synchronized Optional<T> select(Comparator<T> comparator)
	{
		return table.select(comparator);
	}

	@Override
	public synchronized Optional<T> select(Predicate<T> predicate)
	{
		return table.select(predicate);
	}

	@Override
	public synchronized Optional<T> select(Predicate<T> predicate, Comparator<T> comparator)
	{
		return table.select(predicate, comparator);
	}

	@Override
	public synchronized int size()
	{
		return table.size();
	}

	@Override
	public synchronized long count(Predicate<T> predicate)
	{
		return table.count(predicate);
	}

	@Override
	public void addObserver(Observer<T> observer)
	{
		table.addObserver(observer);
	}

	@Override
	public void remObserver(Observer<T> observer)
	{
		table.remObserver(observer);
	}
}
