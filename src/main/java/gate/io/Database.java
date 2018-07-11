package gate.io;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Database<T> extends Observable<T>
{

	void delete(String tableName, Predicate<T> predicate);

	void delete(Predicate<T> predicate);

	void delete(Collection<T> values);

	void delete(List<T> values);

	void delete(T... values);

	void delete(String tableName, Collection<T> values);

	void delete(String tableName, T... values);

	void drop(String tableName);

	void drop();

	void insert(String tableName, Collection<T> values);

	void insert(String tableName, T... values);

	boolean isEmpty();

	boolean isEmpty(String tableName);

	List<T> search();

	List<T> search(Predicate<T> predicate);

	List<T> search(Comparator<T> comparator);

	List<T> search(Predicate<T> predicate, Comparator<T> comparator);

	List<T> search(String tableName);

	List<T> search(String tableName, Predicate<T> predicate);

	List<T> search(String tableName, Comparator<T> comparator);

	List<T> search(String tableName, Predicate<T> predicate, Comparator<T> comparator);

	Optional<T> select();

	Optional<T> select(Predicate<T> predicate);

	Optional<T> select(Comparator<T> comparator);

	Optional<T> select(Predicate<T> predicate, Comparator<T> comparator);

	Optional<T> select(String tableName);

	Optional<T> select(String tableName, Predicate<T> predicate);

	Optional<T> select(String tableName, Comparator<T> comparator);

	Optional<T> select(String tableName, Predicate<T> predicate, Comparator<T> comparator);

	int size();

	int size(String tableName);

	long count(String tableName, Predicate<T> predicate);

	public static <T> Database<T> json(Class<T> type, File folder)
	{
		return JsonDatabase.create(type, folder);
	}

	public static <T> Database<T> jsonConcurrent(Class<T> type, File folder)
	{
		return new ConcurrentDatabase(JsonDatabase.create(type, folder));
	}

	public static <T extends Serializable> Database<T> serializable(Class<T> type, File folder)
	{
		return SerializableDatabase.create(type, folder);
	}

	public static <T extends Serializable> Database<T> serializableConcurrent(Class<T> type, File folder)
	{
		return new ConcurrentDatabase(SerializableDatabase.create(type, folder));
	}
}
