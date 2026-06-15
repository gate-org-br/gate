package gate.util;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class GuardedList<T> implements List<T>
{
	private List<T> values;
	private final List<InsertGuard<T>> insertGuards;
	private final List<RemoveGuard<T>> removeGuards;

	private GuardedList()
	{
		this(new ArrayList<>());
	}

	private GuardedList(List<T> values)
	{
		this(values, new ArrayList<>(), new ArrayList<>());
	}

	private GuardedList(List<T> values,
	                    List<InsertGuard<T>> insertGuards,
	                    List<RemoveGuard<T>> removeGuards)
	{
		this.values = values;
		this.insertGuards = insertGuards;
		this.removeGuards = removeGuards;
	}

	public static <T> GuardedList<T> of()
	{
		return new GuardedList<>();
	}

	public static <T> GuardedList<T> of(List<T> values)
	{
		return new GuardedList<>(values);
	}

	public static <T> Builder<T> builder()
	{
		return new Builder<>(new ArrayList<>());
	}

	public static <T> Builder<T> builder(List<T> values)
	{
		return new Builder<>(values);
	}


	private GuardedList<T> onInsert(InsertGuard<T> guard)
	{
		Objects.requireNonNull(guard);

		List<T> previous = new ArrayList<>();
		for (T value : values)
		{
			guard.check(new GuardedList<>(previous), value);
			previous.add(value);
		}

		insertGuards.add(guard);
		return this;
	}

	private GuardedList<T> onRemove(RemoveGuard<T> guard)
	{
		removeGuards.add(Objects.requireNonNull(guard));
		return this;
	}

	private GuardedList<T> removeInsertGuard(InsertGuard<T> guard)
	{
		insertGuards.remove(guard);
		return this;
	}

	private GuardedList<T> removeRemoveGuard(RemoveGuard<T> guard)
	{
		removeGuards.remove(guard);
		return this;
	}

	private void checkInsert(T value)
	{
		insertGuards.forEach(guard -> guard.check(this, value));
	}

	private void checkRemove(T value)
	{
		removeGuards.forEach(guard -> guard.check(this, value));
	}

	@Override
	public int size() {return values.size();}
	@Override
	public boolean isEmpty() {return values.isEmpty();}
	@Override
	public boolean contains(Object o) {return values.contains(o);}
	@Override
	public Object[] toArray() {return values.toArray();}
	@Override
	public <T1> T1[] toArray(T1[] a) {return values.toArray(a);}

	@Override
	public Iterator<T> iterator()
	{
		Iterator<T> iterator = values.iterator();

		return new Iterator<>()
		{
			private T current;
			private boolean removable;

			@Override
			public boolean hasNext() {return iterator.hasNext();}

			@Override
			public T next()
			{
				current = iterator.next();
				removable = true;
				return current;
			}

			@Override
			public void remove()
			{
				if (!removable)
					throw new IllegalStateException();

				checkRemove(current);
				iterator.remove();
				current = null;
				removable = false;
			}
		};
	}

	@Override
	public boolean add(T value)
	{
		checkInsert(value);
		return values.add(value);
	}

	@Override
	public boolean remove(Object object)
	{
		int index = values.indexOf(object);
		if (index < 0)
			return false;

		remove(index);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> collection)
	{
		return values.containsAll(collection);
	}

	@Override
	public boolean addAll(Collection<? extends T> collection)
	{
		boolean changed = false;
		for (T value : collection)
			changed |= add(value);
		return changed;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> collection)
	{
		if (collection.isEmpty())
			return false;

		ListIterator<T> iterator = values.listIterator(index);
		for (T value : collection)
		{
			checkInsert(value);
			iterator.add(value);
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> collection)
	{
		boolean changed = false;
		for (Iterator<T> iterator = iterator(); iterator.hasNext(); )
			if (collection.contains(iterator.next()))
			{
				iterator.remove();
				changed = true;
			}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> collection)
	{
		boolean changed = false;
		for (Iterator<T> iterator = iterator(); iterator.hasNext(); )
			if (!collection.contains(iterator.next()))
			{
				iterator.remove();
				changed = true;
			}
		return changed;
	}

	@Override
	public void clear()
	{
		for (Iterator<T> iterator = iterator(); iterator.hasNext(); )
		{
			iterator.next();
			iterator.remove();
		}
	}

	@Override
	public T get(int index)
	{
		return values.get(index);
	}

	@Override
	public T set(int index, T element)
	{
		T previous = values.get(index);
		checkRemove(previous);
		checkInsert(element);
		return values.set(index, element);
	}

	@Override
	public void add(int index, T element)
	{
		ListIterator<T> iterator = values.listIterator(index);
		checkInsert(element);
		iterator.add(element);
	}

	@Override
	public T remove(int index)
	{
		T previous = values.get(index);
		checkRemove(previous);
		return values.remove(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return values.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return values.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		ListIterator<T> iterator = values.listIterator(index);

		return new ListIterator<>()
		{
			private T current;
			private boolean mutable;

			@Override
			public boolean hasNext() {return iterator.hasNext();}
			@Override
			public T next()
			{
				current = iterator.next();
				mutable = true;
				return current;
			}
			@Override
			public boolean hasPrevious() {return iterator.hasPrevious();}
			@Override
			public T previous()
			{
				current = iterator.previous();
				mutable = true;
				return current;
			}
			@Override
			public int nextIndex() {return iterator.nextIndex();}
			@Override
			public int previousIndex() {return iterator.previousIndex();}

			@Override
			public void remove()
			{
				if (!mutable)
					throw new IllegalStateException();

				checkRemove(current);
				iterator.remove();
				current = null;
				mutable = false;
			}

			@Override
			public void set(T element)
			{
				if (!mutable)
					throw new IllegalStateException();

				checkRemove(current);
				checkInsert(element);
				iterator.set(element);
				current = element;
			}

			@Override
			public void add(T element)
			{
				checkInsert(element);
				iterator.add(element);
				current = null;
				mutable = false;
			}
		};
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		return new GuardedList<>(values.subList(fromIndex, toIndex), insertGuards, removeGuards);
	}

	@Override
	public boolean equals(Object object)
	{
		return values.equals(object);
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String toString()
	{
		return values.toString();
	}

	public GuardedList<T> replace(List<? extends T> values)
	{
		List<T> next = values != null ? new ArrayList<>(values) : new ArrayList<>();
		var staged = new GuardedList<>(new ArrayList<>(this.values), insertGuards, removeGuards);

		staged.clear();
		staged.addAll(next);

		this.values = next;
		return this;
	}

	public static final class Builder<T>
	{
		private final GuardedList<T> list;

		private Builder(List<T> values)
		{
			this.list = new GuardedList<>(values);
		}

		public Builder<T> onInsert(InsertGuard<T> guard)
		{
			list.onInsert(guard);
			return this;
		}

		public Builder<T> onRemove(RemoveGuard<T> guard)
		{
			list.onRemove(guard);
			return this;
		}

		public ConditionalInsert<T> whenInsert(ListCondition<T> condition)
		{
			return whenInsert((list, value) -> condition.test(list));
		}

		public ConditionalInsert<T> whenInsert(ValueCondition<T> condition)
		{
			return whenInsert((list, value) -> condition.test(value));
		}

		public ConditionalInsert<T> whenInsert(BiPredicate<GuardedList<T>, T> condition)
		{
			return new ConditionalInsert<>(this, condition);
		}

		public ConditionalRemove<T> whenRemove(ListCondition<T> condition)
		{
			return whenRemove((list, value) -> condition.test(list));
		}

		public ConditionalRemove<T> whenRemove(ValueCondition<T> condition)
		{
			return whenRemove((list, value) -> condition.test(value));
		}

		public ConditionalRemove<T> whenRemove(BiPredicate<GuardedList<T>, T> condition)
		{
			return new ConditionalRemove<>(this, condition);
		}

		public GuardedList<T> build()
		{
			return list;
		}
	}

	public record ConditionalInsert<T>(Builder<T> builder,
	                                   BiPredicate<GuardedList<T>, T> condition)
	{
		public Builder<T> thenThrow(RuntimeException exception)
		{
			return thenThrow(() -> exception);
		}

		public Builder<T> thenThrow(Supplier<? extends RuntimeException> exception)
		{
			Objects.requireNonNull(condition);
			Objects.requireNonNull(exception);
			return builder.onInsert((list, value) ->
			{
				if (condition.test(list, value))
					throw exception.get();
			});
		}
	}

	public record ConditionalRemove<T>(Builder<T> builder,
	                                   BiPredicate<GuardedList<T>, T> condition)
	{
		public Builder<T> thenThrow(RuntimeException exception)
		{
			return thenThrow(() -> exception);
		}

		public Builder<T> thenThrow(Supplier<? extends RuntimeException> exception)
		{
			Objects.requireNonNull(condition);
			Objects.requireNonNull(exception);
			return builder.onRemove((list, value) ->
			{
				if (condition.test(list, value))
					throw exception.get();
			});
		}
	}

	@FunctionalInterface
	public interface ListCondition<T>
	{
		boolean test(GuardedList<T> list);
	}

	@FunctionalInterface
	public interface ValueCondition<T>
	{
		boolean test(T value);
	}

	@FunctionalInterface
	public interface InsertGuard<T>
	{
		void check(GuardedList<T> list, T value);
	}

	@FunctionalInterface
	public interface RemoveGuard<T>
	{
		void check(GuardedList<T> list, T value);
	}
}