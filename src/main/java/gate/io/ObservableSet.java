package gate.io;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ObservableSet<T> implements Set<T>, Observable<T>
{

	private final Set<T> values;
	private final List<Observer<T>> observers = new CopyOnWriteArrayList<>();

	private ObservableSet(Set<T> values)
	{
		this.values = values;
	}

	public static <T> ObservableSet<T> of(Set<T> values)
	{
		return new ObservableSet<>(values);
	}

	public static <T> ObservableSet<T> of(Set<T> values, List<Observer<T>> observers)
	{
		var result = new ObservableSet<T>(values);
		observers.forEach(result::addObserver);
		return result;
	}

	public static <T> ObservableSet<T> of(Set<T> values, Observer<T>... observers)
	{
		var result = new ObservableSet<T>(values);
		Stream.of(observers).forEach(result::addObserver);
		return result;
	}

	@Override
	public boolean add(T value)
	{
		if (values.add(value))
		{
			observers.forEach(e -> e.onUpdate());
			return true;
		}

		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> collection)
	{
		if (values.addAll(collection))
		{
			observers.forEach(e -> e.onUpdate());
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object value)
	{
		if (values.remove(value))
		{
			observers.forEach(e -> e.onUpdate());
			return true;
		}

		return false;
	}

	@Override
	public boolean removeAll(Collection<?> collection)
	{
		if (values.removeAll(collection))
		{
			observers.forEach(e -> e.onUpdate());
			return true;
		}

		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection)
	{
		if (values.retainAll(collection))
		{
			observers.forEach(e -> e.onUpdate());
			return true;
		}

		return false;
	}

	@Override
	public void clear()
	{
		if (!values.isEmpty())
		{
			values.clear();
			observers.forEach(e -> e.onUpdate());
		}
	}

	@Override
	public Iterator<T> iterator()
	{
		var iterator = values.iterator();
		return new Iterator<T>()
		{

			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public T next()
			{
				return iterator.next();
			}

			@Override
			public void remove()
			{
				iterator.remove();
				observers.forEach(Observer::onUpdate);
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
	public boolean equals(Object o)
	{
		return o instanceof ObservableSet && values.equals(o);
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}
}
