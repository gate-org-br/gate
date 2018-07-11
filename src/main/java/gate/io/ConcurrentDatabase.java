package gate.io;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ConcurrentDatabase<T extends Serializable> implements Database<T>
{

	private final Database<T> database;

	ConcurrentDatabase(Database database)
	{
		this.database = database;
	}

	@Override
	public synchronized void delete(String tableName, Predicate<T> predicate)
	{
		database.delete(tableName, predicate);
	}

	@Override
	public synchronized void delete(Predicate<T> predicate)
	{
		database.delete(predicate);
	}

	@Override
	public synchronized void delete(Collection<T> values)
	{
		database.delete(values);
	}

	@Override
	public synchronized void delete(T... values)
	{
		database.delete(values);
	}

	@Override
	public synchronized void delete(List<T> values)
	{
		database.delete(values);
	}

	@Override
	public synchronized void delete(String tableName, Collection<T> values)
	{
		database.delete(tableName, values);
	}

	@Override
	public synchronized void delete(String tableName, T... values)
	{
		database.delete(tableName, values);
	}

	@Override
	public synchronized void drop(String tableName)
	{
		database.drop(tableName);
	}

	@Override
	public synchronized void drop()
	{
		database.drop();
	}

	@Override
	public synchronized void insert(String tableName, Collection<T> values)
	{
		database.insert(tableName, values);
	}

	@Override
	public synchronized void insert(String tableName, T... values)
	{
		database.insert(tableName, values);
	}

	@Override
	public synchronized boolean isEmpty()
	{
		return database.isEmpty();
	}

	@Override
	public synchronized boolean isEmpty(String tableName)
	{
		return database.isEmpty(tableName);
	}

	@Override
	public void addObserver(Observer<T> observer)
	{
		database.addObserver(observer);
	}

	@Override
	public void remObserver(Observer<T> observer)
	{
		database.remObserver(observer);
	}

	@Override
	public synchronized List<T> search()
	{
		return database.search();
	}

	@Override
	public synchronized List<T> search(Predicate<T> predicate)
	{
		return database.search(predicate);
	}

	@Override
	public synchronized List<T> search(Comparator<T> comparator)
	{
		return database.search(comparator);
	}

	@Override
	public synchronized List<T> search(Predicate<T> predicate, Comparator<T> comparator)
	{
		return database.search(predicate, comparator);
	}

	@Override
	public synchronized List<T> search(String tableName)
	{
		return database.search(tableName);
	}

	@Override
	public synchronized List<T> search(String tableName, Predicate<T> predicate)
	{
		return database.search(tableName, predicate);
	}

	@Override
	public synchronized List<T> search(String tableName, Comparator<T> comparator)
	{
		return database.search(tableName, comparator);
	}

	@Override
	public synchronized List<T> search(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return database.search(tableName, predicate, comparator);
	}

	@Override
	public synchronized Optional<T> select()
	{
		return database.select();
	}

	@Override
	public synchronized Optional<T> select(Predicate<T> predicate)
	{
		return database.select(predicate);
	}

	@Override
	public synchronized Optional<T> select(Comparator<T> comparator)
	{
		return database.select(comparator);
	}

	@Override
	public synchronized Optional<T> select(Predicate<T> predicate, Comparator<T> comparator)
	{
		return database.select(predicate, comparator);
	}

	@Override
	public synchronized Optional<T> select(String tableName)
	{
		return database.select(tableName);
	}

	@Override
	public synchronized Optional<T> select(String tableName, Predicate<T> predicate)
	{
		return database.select(tableName, predicate);
	}

	@Override
	public synchronized Optional<T> select(String tableName, Comparator<T> comparator)
	{
		return database.select(tableName, comparator);
	}

	@Override
	public synchronized Optional<T> select(String tableName, Predicate<T> predicate, Comparator<T> comparator)
	{
		return database.select(tableName, predicate, comparator);
	}

	@Override
	public synchronized int size()
	{
		return database.size();
	}

	@Override
	public synchronized int size(String tableName)
	{
		return database.size(tableName);
	}

	@Override
	public synchronized long count(String tableName, Predicate<T> predicate)
	{
		return database.count(tableName, predicate);
	}
}
