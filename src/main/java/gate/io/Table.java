package gate.io;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Table<T> extends Observable<T>
{

	void delete(Predicate<T> predicate);

	void delete(Collection<T> values);

	void delete(T... values);

	void drop();

	File getFile();

	void insert(Collection<T> values);

	void insert(T... values);

	boolean isEmpty();

	List<T> search();

	long count(Predicate<T> predicate);

	List<T> search(Comparator<T> comparator);

	List<T> search(Predicate<T> predicate);

	List<T> search(Predicate<T> predicate, Comparator<T> comparator);

	Optional<T> select();

	Optional<T> select(Comparator<T> comparator);

	Optional<T> select(Predicate<T> predicate);

	Optional<T> select(Predicate<T> predicate, Comparator<T> comparator);

	int size();

	public static <T extends Serializable> Table<T> serializable(Class<T> type, File file)
	{
		return SerializableTable.create(type, file);
	}

	public static <T> Table<T> json(Class<T> type, File file)
	{
		return JsonTable.create(type, file);
	}

	public static <T extends Serializable> Table<T> serializableConcurrent(Class<T> type, File file)
	{
		return new ConcurrentTable(SerializableTable.create(type, file));
	}

	public static <T extends Serializable> Table<T> jsonConcurrent(Class<T> type, File file)
	{
		return new ConcurrentTable(JsonTable.create(type, file));
	}

}
