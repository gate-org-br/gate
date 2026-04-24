package gate.sql.insert;

import gate.sql.Formatter;
import gate.sql.columnMapper.ColumnMapper;
import gate.sql.statement.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * Compiled insert builder for a table.
 */
public class TableInsert implements Insert, Sentence.Compiled.Builder
{

	private final String table;
	private final boolean ignore;
	private final List<Object> values = new ArrayList<>();
	private final StringJoiner columns = new StringJoiner(", ", "(", ")");
	private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

	TableInsert(String table, boolean ignore)
	{
		this.table = Formatter.identifier(Objects.requireNonNull(table));
		this.ignore = ignore;
	}

	public TableInsert set(String column, Object value)
	{
		columns.add(Formatter.identifier(Objects.requireNonNull(column)));
		parameters.add("?");
		values.add(value);
		return this;
	}

	public <T> TableInsert set(Class<T> type, String column, T value)
	{
		values.add(value);
		ColumnMapper.getColumnMapper(Objects.requireNonNull(type))
				.getColumns(Objects.requireNonNull(column))
				.map(Formatter::identifier)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		return this;
	}

	public <E> BatchInsert<E> from(Class<E> type)
	{
		return new BatchInsert<>(table, type, ignore);
	}

	public When when(boolean assertion)
	{
		return new When(assertion);
	}

	@Override
	public Sentence.Compiled build()
	{
		return Sentence.of(toString()).parameters(values);
	}

	@Override
	public String toString()
	{
		return "insert " + (ignore ? "ignore " : "") + "into " + table + " " + columns + " values "
		       + parameters;
	}

	public class When
	{
		private final boolean assertion;

		private When(boolean assertion)
		{
			this.assertion = assertion;
		}

		public TableInsert set(String column, Supplier<?> supplier)
		{
			return assertion ? TableInsert.this.set(column, supplier.get()) : TableInsert.this;
		}

		public <T> TableInsert set(Class<T> type, String column, Supplier<T> supplier)
		{
			return assertion ? TableInsert.this.set(type, column, supplier.get()) : TableInsert.this;
		}

		public When when(boolean assertion)
		{
			return new When(this.assertion && assertion);
		}

		@Override
		public String toString()
		{
			return TableInsert.this.toString();
		}
	}
}