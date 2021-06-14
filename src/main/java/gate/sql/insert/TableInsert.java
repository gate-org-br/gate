package gate.sql.insert;

import gate.converter.Converter;
import gate.sql.statement.Sentence;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Insert sentence builder for a table.
 */
public class TableInsert implements Insert
{

	private final String table;
	private final Set<String> insert
		= new HashSet<>();

	TableInsert(String table)
	{
		this.table = table;
		insert.add("insert");
	}

	/**
	 * Binds the insert statement to a list of entities.
	 *
	 *
	 * @param type the type of the entity where parameter values are to be extracted
	 *
	 * @return the same builder with the associated entities
	 */
	public <T> Prepared<T> from(Class<T> type)
	{
		return new Prepared<>(type);
	}

	/**
	 * Adds an ignore modifier to the select sentence.
	 *
	 * @return the same builder with the added column
	 */
	public TableInsert ignore()
	{
		insert.add("ignore");
		return this;
	}

	/**
	 * Adds a new column to be persisted.
	 *
	 * @param column the column to be added
	 *
	 * @return the same builder with the added column
	 */
	public Generic set(String column)
	{
		return new Generic().set(column);
	}

	/**
	 * Adds a new column to be persisted.
	 *
	 *
	 * @param type type of the column to be persisted
	 * @param column the column to be persisted
	 *
	 * @return the same builder with the added column
	 */
	public <T> Generic set(Class<T> type, String column)
	{
		return new Generic().set(type, column);
	}

	/**
	 * Adds a new column to be persisted with the specified value.
	 *
	 * @param column the column to be persisted
	 * @param value the value associated
	 *
	 * @return the same builder with the added column
	 */
	public Compiled set(String column, Object value)
	{
		return new Compiled().set(column, value);
	}

	/**
	 * Adds a new column to be persisted with the specified value.
	 *
	 *
	 * @param type type of the column to be added
	 * @param column the column to be added
	 * @param value the value associated
	 *
	 * @return the same builder with the added column
	 */
	public <T> Compiled set(Class<T> type, String column, T value)
	{
		return new Compiled().set(type, column, value);
	}

	/**
	 * Adds the next column to the builder if previous specified condition is true.
	 *
	 * @param assertion the condition to be checked
	 *
	 * @return the same builder with the applied condition
	 */
	public When when(boolean assertion)
	{
		return assertion ? new When() : new DisabledWhen();
	}

	/**
	 * Insert sentence builder for a table with no values specified.
	 */
	public class Generic implements Sentence.Builder
	{

		private final StringJoiner columns = new StringJoiner(", ", "(", ")");
		private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

		private Generic()
		{
		}

		/**
		 * Adds a new column to the builder.
		 *
		 * @param column the column to be added
		 *
		 * @return the same builder with the added column
		 */
		public Generic set(String column)
		{
			columns.add(column);
			parameters.add("?");
			return this;
		}

		/**
		 * Adds a new column to the builder.
		 *
		 *
		 * @param column the column to be added
		 * @param type type of the column to be added
		 *
		 * @return the same builder with the added column
		 */
		public <T> Generic set(Class<T> type, String column)
		{
			Converter.getConverter(type)
				.getColumns(column)
				.forEach(this::set);
			return this;
		}

		@Override
		public Sentence build()
		{
			return Sentence.of(toString());
		}

		@Override
		public String toString()
		{
			return String.join(" ", insert) + " into " + table + " " + columns + " values " + parameters;
		}
	}

	/**
	 * SQL insert sentence builder for a table with values specified and ready for execution.
	 */
	public class Compiled implements Sentence.Compiled.Builder
	{

		private final List<Object> values = new ArrayList<>();
		private final StringJoiner columns = new StringJoiner(", ", "(", ")");
		private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

		private Compiled()
		{
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param column the column to be added
		 * @param value the value associated
		 *
		 * @return the same builder with the added column and value
		 */
		public Compiled set(String column, Object value)
		{
			columns.add(column);
			parameters.add("?");
			values.add(value);
			return this;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 *
		 * @param type type of the column to be added
		 * @param column the column to be added
		 * @param value the value associated
		 *
		 * @return the same builder with the added column and value
		 */
		public <T> Compiled set(Class<T> type, String column, T value)
		{
			values.add(value);
			Converter.getConverter(type)
				.getColumns(column)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
			return this;
		}

		@Override
		public Sentence.Compiled build()
		{
			return Sentence.of(toString()).parameters(values);
		}

		/**
		 * Adds the next column to the builder if previous specified condition is true.
		 *
		 * @param assertion the condition to be checked
		 *
		 * @return the same builder with the applied condition
		 */
		public When when(boolean assertion)
		{
			return assertion ? new When() : new DisabledWhen();
		}

		@Override
		public String toString()
		{
			return String.join(" ", insert) + " into " + table + " " + columns + " values " + parameters;
		}

		public class When
		{

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 * @param column the column to be added
			 * @param value the value associated
			 *
			 * @return the same builder with the added column
			 */
			public Compiled set(String column, Object value)
			{
				return Compiled.this.set(column, value);
			}

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 * @param column the column to be added
			 * @param supplier the supplier of the value associated
			 *
			 * @return the same builder with the added column and value
			 */
			public Compiled setIfTrue(String column, Supplier<Object> supplier)
			{
				return Compiled.this.set(column, supplier.get());
			}

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 *
			 * @param column the column to be added
			 * @param type type of the column to be added
			 * @param value the value associated
			 *
			 * @return the same builder with the added column
			 */
			public <T> Compiled set(Class<T> type, String column, T value)
			{
				return Compiled.this.set(type, column, value);
			}

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 *
			 * @param type type of the column to be added
			 * @param column the column to be added
			 * @param supplier the supplier of the value associated
			 *
			 * @return the same builder with the added column and value
			 */
			public <T> Compiled setIfTrue(Class<T> type, String column, Supplier<T> supplier)
			{
				return Compiled.this.set(type, column, supplier.get());
			}

			/**
			 * Adds the next column to the builder if previous specified condition is true.
			 *
			 * @param assertion the condition to be checked
			 *
			 * @return the same builder with the applied condition
			 */
			public When when(boolean assertion)
			{
				return assertion ? this : new DisabledWhen();
			}

			@Override
			public String toString()
			{
				return Compiled.this.toString();
			}
		}

		public class DisabledWhen extends When
		{

			@Override
			public Compiled set(String column, Object value)
			{
				return Compiled.this;
			}

			@Override
			public Compiled setIfTrue(String column, Supplier<Object> supplier)
			{
				return Compiled.this;
			}

			@Override
			public <T> Compiled set(Class<T> type, String column, T value)
			{
				return Compiled.this;
			}

			@Override
			public <T> Compiled setIfTrue(Class<T> type, String column, Supplier<T> supplier)
			{
				return Compiled.this;
			}

			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	/**
	 * SQL insert sentence builder for a table with values specified and ready for execution.
	 *
	 * @param <E> type of the entities to be inserted on database
	 */
	public class Prepared<E> implements Sentence.Extractor.Compiled.Builder
	{

		private final Class<E> type;
		private final List<Function<E, ?>> extractors = new ArrayList<>();
		private final StringJoiner columns = new StringJoiner(", ", "(", ")");
		private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

		private Prepared(Class<E> type)
		{
			this.type = type;
		}

		/**
		 * Adds an ignore modifier to the select sentence.
		 *
		 * @return the same builder with the added column
		 */
		public Prepared<E> ignore()
		{
			insert.add("ignore");
			return this;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param column the column to be added
		 * @param extractor the extractor function associated with the column
		 *
		 * @return the same builder with the added column and value
		 */
		public Prepared<E> set(String column, Function<E, ?> extractor)
		{
			columns.add(column);
			parameters.add("?");
			extractors.add(extractor);
			return this;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param <K> type of the value added
		 * @param column the column to be added
		 * @param type type of the column to be added
		 * @param extractor the extractor function associated with the column
		 *
		 * @return the same builder with the added column and value
		 */
		public <K> Prepared<E> set(Class<K> type, String column, Function<E, K> extractor)
		{
			extractors.add(extractor);
			Converter.getConverter(type)
				.getColumns(column)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
			return this;
		}

		@Override
		public Sentence.Extractor.Compiled<E> build()
		{
			return Sentence.of(toString()).from(type).parameters(extractors);
		}

		@Override
		public String toString()
		{
			return String.join(" ", insert) + " into " + table + " " + columns + " values " + parameters;
		}
	}

	public class When
	{

		/**
		 * Adds a new column to be persisted if the previous specified condition was true.
		 *
		 * @param column the column to be added
		 *
		 * @return the same builder with the added column
		 */
		public Generic set(String column)
		{
			return new Generic().set(column);
		}

		/**
		 * Adds a new column to be persisted if the previous specified condition was true.
		 *
		 *
		 * @param type type of the column to be persisted
		 * @param column the column to be persisted
		 *
		 * @return the same builder with the added column
		 */
		public <T> Generic set(Class<T> type, String column)
		{
			return new Generic().set(type, column);
		}

		/**
		 * Adds a new column to be persisted with the specified value if the previous specified condition was true.
		 *
		 * @param column the column to be persisted
		 * @param value the value associated
		 *
		 * @return the same builder with the added column
		 */
		public Compiled set(String column, Object value)
		{
			return new Compiled().set(column, value);
		}

		/**
		 * Adds a new column to be persisted with the specified value if the previous specified condition was true.
		 *
		 * @param column the column to be persisted
		 * @param supplier the supplier of the value associated
		 *
		 * @return the same builder with the added column
		 */
		public Compiled setIfTrue(String column, Supplier<Object> supplier)
		{
			return new Compiled().set(column, supplier.get());
		}

		/**
		 * Adds a new column to be persisted with the specified value if the previous specified condition was true.
		 *
		 *
		 * @param type type of the column to be added
		 * @param column the column to be added
		 * @param value the value associated
		 *
		 * @return the same builder with the added column
		 */
		public <T> Compiled set(Class<T> type, String column, T value)
		{
			return new Compiled().set(type, column, value);
		}

		/**
		 * Adds a new column to be persisted with the specified value if the previous specified condition was true.
		 *
		 *
		 * @param type type of the column to be added
		 * @param column the column to be added
		 * @param supplier the supplier of the value associated
		 *
		 * @return the same builder with the added column
		 */
		public <T> Compiled setIfTrue(Class<T> type, String column, Supplier<T> supplier)
		{
			return new Compiled().set(type, column, supplier.get());
		}

		/**
		 * Adds the next column to the builder if previous specified condition is true.
		 *
		 * @param assertion the condition to be checked
		 *
		 * @return the same builder with the applied condition
		 */
		public When when(boolean assertion)
		{
			return assertion ? this : new DisabledWhen();
		}

		@Override
		public String toString()
		{
			return TableInsert.this.toString();
		}
	}

	public class DisabledWhen extends When
	{

		@Override
		public Generic set(String column)
		{
			return new Generic();
		}

		@Override
		public <T> Generic set(Class<T> type, String column)
		{
			return new Generic();
		}

		@Override
		public Compiled set(String column, Object value)
		{
			return new Compiled();
		}

		@Override
		public Compiled setIfTrue(String column, Supplier<Object> supplier)
		{
			return new Compiled();
		}

		@Override
		public <T> Compiled set(Class<T> type, String column, T value)
		{
			return new Compiled();
		}

		@Override
		public <T> Compiled setIfTrue(Class<T> type, String column, Supplier<T> supplier)
		{
			return new Compiled();
		}

		@Override
		public When when(boolean assertion)
		{
			return this;
		}
	}
}
