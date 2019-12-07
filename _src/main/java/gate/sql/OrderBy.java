package gate.sql;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderBy
{

	public static Ordering asc(String column)
	{
		return new Ordering().asc(column);
	}

	public static Ordering desc(String column)
	{
		return new Ordering().desc(column);
	}

	public static Ordering parse(String column)
	{
		switch (column.charAt(0))
		{
			case '-':
				return new Ordering().desc(column.substring(1));
			case '+':
				return new Ordering().asc(column.substring(1));
			default:
				return new Ordering().asc(column);
		}
	}

	public static Ordering asc(String... columns)
	{
		Ordering ordering = new Ordering();
		Stream.of(columns).forEach(ordering::asc);
		return ordering;
	}

	public static Ordering desc(String... columns)
	{
		Ordering ordering = new Ordering();
		Stream.of(columns).forEach(ordering::desc);
		return ordering;
	}

	public static class Ordering
	{

		private final Map<String, String> columns
				= new LinkedHashMap<>();

		public Ordering asc(String column)
		{
			columns.put(column, "asc");
			return this;
		}

		public Ordering desc(String column)
		{
			columns.put(column, "desc");
			return this;
		}

		@Override
		public String toString()
		{
			return columns.entrySet().stream().map(e -> e.getKey() + " "
					+ e.getValue()).collect(Collectors.joining(", "));
		}

		public Collection<String> getColumns()
		{
			return columns.keySet();
		}

		public String toString(Function<String, Stream<String>> getColumnNames)
		{
			return columns
					.entrySet()
					.stream()
					.flatMap(entry
							-> getColumnNames
							.apply(entry.getKey())
							.map(column -> column + " " + entry.getValue()))
					.collect(Collectors.joining(", "));
		}
	}
}
