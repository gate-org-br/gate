package gate.sql.replace;

import gate.adapter.columnMapper.ColumnMapper;
import gate.sql.Formatter;
import gate.sql.statement.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Compiled replace builder for a table.
 */
public class TableReplace implements Replace, Sentence.Compiled.Builder
{

	private final String table;
	private final List<Object> values = new ArrayList<>();
	private final StringJoiner columns = new StringJoiner(", ", "(", ")");
	private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

	TableReplace(String table)
	{
		this.table = Formatter.identifier(Objects.requireNonNull(table));
	}

	public TableReplace set(String column, Object value)
	{
		columns.add(Formatter.identifier(Objects.requireNonNull(column)));
		parameters.add("?");
		values.add(value);
		return this;
	}

	public <T> TableReplace set(Class<T> type, String column, T value)
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

	public <E> BatchReplace<E> from(Class<E> type)
	{
		return new BatchReplace<>(table, type);
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
		return "replace into " + table + " " + columns + " values " + parameters;
	}

	public class When
	{
		private final boolean assertion;

		private When(boolean assertion)
		{
			this.assertion = assertion;
		}

		public TableReplace set(String column, Supplier<?> supplier)
		{
			return assertion ? TableReplace.this.set(column, supplier.get()) : TableReplace.this;
		}

		public <T> TableReplace set(Class<T> type, String column, Supplier<T> supplier)
		{
			return assertion ? TableReplace.this.set(type, column, supplier.get()) : TableReplace.this;
		}

		public TableReplace then(Function<TableReplace, TableReplace> function)
		{
			return assertion ? function.apply(TableReplace.this) : TableReplace.this;
		}

		public When when(boolean assertion)
		{
			return new When(this.assertion && assertion);
		}

		@Override
		public String toString()
		{
			return TableReplace.this.toString();
		}
	}
}
