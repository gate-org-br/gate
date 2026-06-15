package gate.util;

import java.util.*;
import java.util.function.*;

public class GuardedSet<T> implements Set<T>
{
	private Set<T> values;
	private final List<InsertGuard<T>> insertGuards;
	private final List<RemoveGuard<T>> removeGuards;

	public GuardedSet()
	{
		this(new LinkedHashSet<>());
	}

	public GuardedSet(Set<T> values)
	{
		this(values, new ArrayList<>(), new ArrayList<>());
	}

	private GuardedSet(Set<T> values,
	                   List<InsertGuard<T>> insertGuards,
	                   List<RemoveGuard<T>> removeGuards)
	{
		this.values = values;
		this.insertGuards = insertGuards;
		this.removeGuards = removeGuards;
	}

	public static <T> GuardedSet<T> of()
	{
		return new GuardedSet<>();
	}

	public static <T> GuardedSet<T> of(Set<T> values)
	{
		return new GuardedSet<>(values);
	}


	public GuardedSet<T> onInsert(InsertGuard<T> guard)
	{
		Objects.requireNonNull(guard);

		Set<T> previous = new LinkedHashSet<>();
		for (T value : values)
		{
			guard.check(new GuardedSet<>(previous), value);
			previous.add(value);
		}

		insertGuards.add(guard);
		return this;
	}

	public GuardedSet<T> onRemove(RemoveGuard<T> guard)
	{
		removeGuards.add(Objects.requireNonNull(guard));
		return this;
	}

	public GuardedSet<T> removeInsertGuard(InsertGuard<T> guard)
	{
		insertGuards.remove(guard);
		return this;
	}

	public GuardedSet<T> removeRemoveGuard(RemoveGuard<T> guard)
	{
		removeGuards.remove(guard);
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

	public ConditionalInsert<T> whenInsert(BiPredicate<GuardedSet<T>, T> condition)
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

	public ConditionalRemove<T> whenRemove(BiPredicate<GuardedSet<T>, T> condition)
	{
		return new ConditionalRemove<>(this, condition);
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
		if (values.contains(value))
			return false;

		checkInsert(value);
		return values.add(value);
	}

	@Override
	public boolean remove(Object object)
	{
		Iterator<T> iterator = iterator();
		while (iterator.hasNext())
			if (Objects.equals(iterator.next(), object))
			{
				iterator.remove();
				return true;
			}
		return false;
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
	public boolean retainAll(Collection<?> collection)
	{
		boolean changed = false;
		for (Iterator<T> iterator = iterator(); iterator.hasNext();)
			if (!collection.contains(iterator.next()))
			{
				iterator.remove();
				changed = true;
			}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> collection)
	{
		boolean changed = false;
		for (Iterator<T> iterator = iterator(); iterator.hasNext();)
			if (collection.contains(iterator.next()))
			{
				iterator.remove();
				changed = true;
			}
		return changed;
	}

	@Override
	public void clear()
	{
		for (Iterator<T> iterator = iterator(); iterator.hasNext();)
		{
			iterator.next();
			iterator.remove();
		}
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

	public GuardedSet<T> replace(Set<? extends T> values)
	{
		Set<T> next = values != null ? new LinkedHashSet<>(values) : new LinkedHashSet<>();
		var staged = new GuardedSet<>(new LinkedHashSet<>(this.values), insertGuards, removeGuards);

		staged.clear();
		staged.addAll(next);

		this.values = next;
		return this;
	}

	public record ConditionalInsert<T>(GuardedSet<T> set,
	                                   BiPredicate<GuardedSet<T>, T> condition)
	{
		public GuardedSet<T> thenThrow(RuntimeException exception)
		{
			return thenThrow(() -> exception);
		}

		public GuardedSet<T> thenThrow(Supplier<? extends RuntimeException> exception)
		{
			Objects.requireNonNull(condition);
			Objects.requireNonNull(exception);
			return set.onInsert((set, value) ->
			{
				if (condition.test(set, value))
					throw exception.get();
			});
		}
	}

	public record ConditionalRemove<T>(GuardedSet<T> set,
	                                   BiPredicate<GuardedSet<T>, T> condition)
	{
		public GuardedSet<T> thenThrow(RuntimeException exception)
		{
			return thenThrow(() -> exception);
		}

		public GuardedSet<T> thenThrow(Supplier<? extends RuntimeException> exception)
		{
			Objects.requireNonNull(condition);
			Objects.requireNonNull(exception);
			return set.onRemove((set, value) ->
			{
				if (condition.test(set, value))
					throw exception.get();
			});
		}
	}

	@FunctionalInterface
	public interface ListCondition<T>
	{
		boolean test(GuardedSet<T> set);
	}

	@FunctionalInterface
	public interface ValueCondition<T>
	{
		boolean test(T value);
	}

	@FunctionalInterface
	public interface InsertGuard<T>
	{
		void check(GuardedSet<T> set, T value);
	}

	@FunctionalInterface
	public interface RemoveGuard<T>
	{
		void check(GuardedSet<T> set, T value);
	}
}
