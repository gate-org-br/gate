package gate.sql.insert;

import java.util.Objects;

/**
 * Factory for inserts with the ignore modifier enabled.
 */
public final class IgnoredInsert
{

	IgnoredInsert()
	{
	}

	public TableInsert into(String table)
	{
		return new TableInsert(table, true);
	}

	public <T> ClassInsert<T> into(Class<T> type)
	{
		return new ClassInsert<>(type, true);
	}

	public <T> ObjectInsert<T> into(T object)
	{
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) Objects.requireNonNull(object).getClass();
		return new ObjectInsert<>(type, object, true);
	}
}
