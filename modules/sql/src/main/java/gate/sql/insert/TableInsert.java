package gate.sql.insert;

import gate.adapter.columnMapper.ColumnMapper;
import gate.sql.Formatter;
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
		Objects.requireNonNull(column);
		columns.add(Formatter.identifier(Objects.requireNonNull(column)));
		parameters.add("?");
		values.add(value);
		return this;
	}

	/**
	 * Adds a column value supplied lazily to this insert sentence.
	 * <p>
	 * If {@code column} is {@code null}, this method leaves the insert unchanged
	 * and does not evaluate {@code value}. This is useful when the target column
	 * is selected conditionally and the absence of a column must also skip the
	 * value computation.
	 *
	 * @param column the column name, or {@code null} to skip this assignment
	 * @param value supplier of the value to be inserted
	 * @return this insert sentence builder
	 */
	public TableInsert set(String column, Supplier<?> value)
	{
		return column != null ? set(column, value.get()) : this;
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
