package gate.util;

import java.util.*;
import java.util.function.*;

public class GuardedMap<K, V> implements Map<K, V>
{
	private Map<K, V> values;
	private final List<InsertGuard<K, V>> insertGuards;
	private final List<RemoveGuard<K, V>> removeGuards;

	public GuardedMap()
	{
		this(new LinkedHashMap<>());
	}

	public GuardedMap(Map<K, V> values)
	{
		this(values, new ArrayList<>(), new ArrayList<>());
	}

	private GuardedMap(Map<K, V> values,
	                   List<InsertGuard<K, V>> insertGuards,
	                   List<RemoveGuard<K, V>> removeGuards)
	{
		this.values = values;
		this.insertGuards = insertGuards;
		this.removeGuards = removeGuards;
	}

	public static <K, V> GuardedMap<K, V> of()
	{
		return new GuardedMap<>();
	}

	public static <K, V> GuardedMap<K, V> of(Map<K, V> values)
	{
		return new GuardedMap<>(values);
	}


	public GuardedMap<K, V> onInsert(InsertGuard<K, V> guard)
	{
		Objects.requireNonNull(guard);

		Map<K, V> previous = new LinkedHashMap<>();
		for (Entry<K, V> entry : values.entrySet())
		{
			guard.check(new GuardedMap<>(previous), entry.getKey(), entry.getValue());
			previous.put(entry.getKey(), entry.getValue());
		}

		insertGuards.add(guard);
		return this;
	}

	public GuardedMap<K, V> onRemove(RemoveGuard<K, V> guard)
	{
		removeGuards.add(Objects.requireNonNull(guard));
		return this;
	}

	public GuardedMap<K, V> removeInsertGuard(InsertGuard<K, V> guard)
	{
		insertGuards.remove(guard);
		return this;
	}

	public GuardedMap<K, V> removeRemoveGuard(RemoveGuard<K, V> guard)
	{
		removeGuards.remove(guard);
		return this;
	}

	public ConditionalInsert<K, V> whenInsert(Condition<GuardedMap<K, V>, K, V> condition)
	{
		return new ConditionalInsert<>(this, condition);
	}

	public ConditionalRemove<K, V> whenRemove(Condition<GuardedMap<K, V>, K, V> condition)
	{
		return new ConditionalRemove<>(this, condition);
	}

	private void checkInsert(K key, V value)
	{
		insertGuards.forEach(guard -> guard.check(this, key, value));
	}

	private void checkRemove(K key, V value)
	{
		removeGuards.forEach(guard -> guard.check(this, key, value));
	}

	@Override
	public int size() {return values.size();}
	@Override
	public boolean isEmpty() {return values.isEmpty();}
	@Override
	public boolean containsKey(Object key) {return values.containsKey(key);}
	@Override
	public boolean containsValue(Object value) {return values.containsValue(value);}
	@Override
	public V get(Object key) {return values.get(key);}

	@Override
	public V put(K key, V value)
	{
		if (values.containsKey(key))
			checkRemove(key, values.get(key));
		checkInsert(key, value);
		return values.put(key, value);
	}

	@Override
	public V remove(Object key)
	{
		if (!values.containsKey(key))
			return null;

		K cast = (K) key;
		V value = values.get(key);
		checkRemove(cast, value);
		return values.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map)
	{
		map.forEach(this::put);
	}

	@Override
	public void clear()
	{
		for (Iterator<Entry<K, V>> iterator = entrySet().iterator(); iterator.hasNext();)
		{
			iterator.next();
			iterator.remove();
		}
	}

	@Override
	public Set<K> keySet()
	{
		return new AbstractSet<>()
		{
			@Override
			public Iterator<K> iterator()
			{
				Iterator<Entry<K, V>> iterator = entrySet().iterator();

				return new Iterator<>()
				{
					@Override
					public boolean hasNext() {return iterator.hasNext();}
					@Override
					public K next() {return iterator.next().getKey();}
					@Override
					public void remove() {iterator.remove();}
				};
			}

			@Override
			public int size() {return GuardedMap.this.size();}
			@Override
			public boolean contains(Object object) {return GuardedMap.this.containsKey(object);}
			@Override
			public boolean remove(Object object) {return GuardedMap.this.remove(object) != null;}
			@Override
			public void clear() {GuardedMap.this.clear();}
		};
	}

	@Override
	public Collection<V> values()
	{
		return new AbstractCollection<>()
		{
			@Override
			public Iterator<V> iterator()
			{
				Iterator<Entry<K, V>> iterator = entrySet().iterator();

				return new Iterator<>()
				{
					@Override
					public boolean hasNext() {return iterator.hasNext();}
					@Override
					public V next() {return iterator.next().getValue();}
					@Override
					public void remove() {iterator.remove();}
				};
			}

			@Override
			public int size() {return GuardedMap.this.size();}
			@Override
			public boolean contains(Object object) {return GuardedMap.this.containsValue(object);}
			@Override
			public void clear() {GuardedMap.this.clear();}
		};
	}

	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return new AbstractSet<>()
		{
			@Override
			public Iterator<Entry<K, V>> iterator()
			{
				Iterator<Entry<K, V>> iterator = values.entrySet().iterator();

				return new Iterator<>()
				{
					private Entry<K, V> current;
					private boolean removable;

					@Override
					public boolean hasNext() {return iterator.hasNext();}

					@Override
					public Entry<K, V> next()
					{
						current = iterator.next();
						removable = true;
						return new GuardedEntry(current);
					}

					@Override
					public void remove()
					{
						if (!removable)
							throw new IllegalStateException();

						checkRemove(current.getKey(), current.getValue());
						iterator.remove();
						current = null;
						removable = false;
					}
				};
			}

			@Override
			public int size() {return GuardedMap.this.size();}
			@Override
			public void clear() {GuardedMap.this.clear();}
			@Override
			public boolean contains(Object object) {return values.entrySet().contains(object);}
			@Override
			public boolean remove(Object object)
			{
				if (!(object instanceof Entry<?, ?> entry))
					return false;

				if (!Objects.equals(values.get(entry.getKey()), entry.getValue())
						|| !values.containsKey(entry.getKey()))
					return false;

				GuardedMap.this.remove(entry.getKey());
				return true;
			}
		};
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

	public GuardedMap<K, V> replace(Map<? extends K, ? extends V> values)
	{
		Map<K, V> next = values != null ? new LinkedHashMap<>(values) : new LinkedHashMap<>();
		var staged = new GuardedMap<>(new LinkedHashMap<>(this.values), insertGuards, removeGuards);

		staged.clear();
		staged.putAll(next);

		this.values = next;
		return this;
	}

	private class GuardedEntry implements Entry<K, V>
	{
		private final Entry<K, V> entry;

		private GuardedEntry(Entry<K, V> entry)
		{
			this.entry = entry;
		}

		@Override
		public K getKey() {return entry.getKey();}
		@Override
		public V getValue() {return entry.getValue();}

		@Override
		public V setValue(V value)
		{
			checkRemove(entry.getKey(), entry.getValue());
			checkInsert(entry.getKey(), value);
			return entry.setValue(value);
		}

		@Override
		public boolean equals(Object object)
		{
			return entry.equals(object);
		}

		@Override
		public int hashCode()
		{
			return entry.hashCode();
		}
	}

	public record ConditionalInsert<K, V>(GuardedMap<K, V> map,
	                                      Condition<GuardedMap<K, V>, K, V> condition)
	{
		public GuardedMap<K, V> thenThrow(RuntimeException exception)
		{
			return thenThrow(() -> exception);
		}

		public GuardedMap<K, V> thenThrow(Supplier<? extends RuntimeException> exception)
		{
			Objects.requireNonNull(condition);
			Objects.requireNonNull(exception);
			return map.onInsert((map, key, value) ->
			{
				if (condition.test(map, key, value))
					throw exception.get();
			});
		}
	}

	public record ConditionalRemove<K, V>(GuardedMap<K, V> map,
	                                      Condition<GuardedMap<K, V>, K, V> condition)
	{
		public GuardedMap<K, V> thenThrow(RuntimeException exception)
		{
			return thenThrow(() -> exception);
		}

		public GuardedMap<K, V> thenThrow(Supplier<? extends RuntimeException> exception)
		{
			Objects.requireNonNull(condition);
			Objects.requireNonNull(exception);
			return map.onRemove((map, key, value) ->
			{
				if (condition.test(map, key, value))
					throw exception.get();
			});
		}
	}

	@FunctionalInterface
	public interface Condition<T, K, V>
	{
		boolean test(T object, K key, V value);
	}

	@FunctionalInterface
	public interface InsertGuard<K, V>
	{
		void check(GuardedMap<K, V> map, K key, V value);
	}

	@FunctionalInterface
	public interface RemoveGuard<K, V>
	{
		void check(GuardedMap<K, V> map, K key, V value);
	}
}
