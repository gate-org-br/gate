package gate.sql.update;

import gate.converter.Converter;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.ExtractorCondition;
import gate.sql.condition.GenericCondition;
import gate.sql.statement.Sentence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a an update statement bound to a table name.
 */
public class TableUpdate implements Update
{

	private final String table;

	TableUpdate(String name)
	{
		table = name;
	}

	/**
	 * Binds the update statement to a list of entities.
	 *
	 * @param <T> type of entities to be associated with the update statement
	 * @param entities the list of entities to be associated with the update statement
	 *
	 * @return the same builder with the associated entities
	 */
	public <T> Prepared<T> entities(List<T> entities)
	{
		return new Prepared<>(entities);
	}

	/**
	 * Adds a new column to be updated.
	 *
	 * @param column the column to be updated
	 *
	 * @return the same update sentence builder with the added column
	 */
	public Generic set(String column)
	{
		return new Generic().set(column);
	}

	/**
	 * Adds a new column to be updated.
	 *
	 * @param <T> type of the column to be updated
	 * @param type type of the column to be updated
	 * @param column the column to be updated
	 *
	 * @return the same update sentence builder with the added column
	 */
	public <T> Generic set(Class<T> type, String column)
	{
		return new Generic().set(type, column);
	}

	/**
	 * Adds a new column to be updated with the specified value.
	 *
	 * @param column the column to be updated
	 * @param value the new value of the column
	 *
	 * @return the same update sentence builder with the added column
	 */
	public Compiled set(String column, Object value)
	{
		return new Compiled().set(column, value);
	}

	/**
	 * Adds a new column to be updated with the specified value.
	 *
	 * @param <T> type of the column to be updated
	 * @param column the column to be updated
	 * @param type type of the column to be updated
	 * @param value the new value of the column
	 *
	 * @return the same update sentence builder with the added column
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

	@Override
	public String toString()
	{
		return "update " + table;
	}

	/**
	 * Represents a an update statement bound to a table name and column names.
	 */
	public class Generic implements Sentence.Builder
	{

		private final StringJoiner columns = new StringJoiner(", ");

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
			columns.add(column + " = ?");
			return this;
		}

		/**
		 * Adds a new column to the builder.
		 *
		 * @param <T> type of the column added
		 * @param type type of the column to be added
		 * @param column the column to be added
		 *
		 * @return the same builder with the added column
		 */
		public <T> Generic set(Class<T> type, String column)
		{
			Converter.getConverter(type).getColumns(column).forEach(this::set);
			return this;
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public Sentence.Compiled.Builder where(ConstantCondition condition)
		{
			return () -> Sentence.of(Generic.this + " where " + condition)
				.parameters(Collections.singletonList(Collections.emptyList()));
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public Sentence.Builder where(GenericCondition condition)
		{
			return () -> Sentence.of(Generic.this + " where " + condition);
		}

		/**
		 * Creates a sentence with the specified columns and no condition associated.
		 *
		 * @return a sentence with the specified columns and no condition associated
		 */
		@Override
		public Sentence build()
		{
			return Sentence.of(toString());
		}

		@Override
		public String toString()
		{
			return TableUpdate.this + " set " + columns;
		}

		public class When
		{

			/**
			 * Adds a new column to be updated.
			 *
			 * @param column the column to be updated
			 *
			 * @return the same update sentence builder with the added column
			 */
			public Generic set(String column)
			{
				return Generic.this.set(column);
			}

			/**
			 * Adds a new column to be updated.
			 *
			 * @param <T> type of the column to be updated
			 * @param type type of the column to be updated
			 * @param column the column to be updated
			 *
			 * @return the same update sentence builder with the added column
			 */
			public <T> Generic set(Class<T> type, String column)
			{
				return Generic.this.set(type, column);
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
				return TableUpdate.this.toString();
			}
		}

		public class DisabledWhen extends When
		{

			@Override
			public Generic set(String column)
			{
				return Generic.this;
			}

			@Override
			public <T> Generic set(Class<T> type, String column)
			{
				return Generic.this;
			}

			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	public class Compiled implements Sentence.Compiled.Builder
	{

		private final StringJoiner columns = new StringJoiner(", ");
		private final List<Object> values = new ArrayList<>();

		private Compiled()
		{
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param column the column to be added
		 * @param value the value associated
		 *
		 * @return the same builder with the added column
		 */
		public Compiled set(String column, Object value)
		{
			if (value != null)
				return set((Class<Object>) value.getClass(), column, value);

			columns.add(column + " = ?");
			values.add(value);
			return this;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param <T> type of the column added
		 * @param column the column to be added
		 * @param type type of the column to be added
		 * @param value the value associated
		 *
		 * @return the same builder with the added column
		 */
		public <T> Compiled set(Class<T> type, String column, T value)
		{
			values.add(value);
			Converter.getConverter(type)
				.getColumns(column)
				.map(e -> e + " = ?")
				.forEach(columns::add);
			return this;
		}

		/**
		 * Binds a condition to the update statement
		 *
		 * @param condition to be bound to the update statement
		 *
		 * @return A SQLBuilder with the conditions specified
		 */
		public Sentence.Compiled.Builder where(ConstantCondition condition)
		{
			return () -> Sentence.of(Compiled.this + " where " + condition).parameters(values);
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public Sentence.Compiled.Builder where(CompiledCondition condition)
		{
			return () -> Sentence.of(Compiled.this + " where " + condition)
				.parameters(Stream.concat(values.stream(), condition.getParameters()).collect(Collectors.toList()));
		}

		/**
		 * Creates a sentence with the specified columns, values and no condition associated.
		 *
		 * @return a sentence with the specified columns, values and no condition associated
		 */
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
			return TableUpdate.this + " set " + columns;
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
			 * @return the same builder with the added column
			 */
			public Compiled setIfTrue(String column, Supplier<Object> supplier)
			{
				return Compiled.this.set(column, supplier.get());
			}

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 * @param <T> type of the column added
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
			 * @param <T> type of the column added
			 * @param column the column to be added
			 * @param type type of the column to be added
			 * @param supplier the supplier of the value associated
			 *
			 * @return the same builder with the added column
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
	public class Prepared<E> implements Sentence.Prepared.Compiled.Builder
	{

		private final Collection<E> entities;
		private final StringJoiner columns = new StringJoiner(", ");
		private final List<Function<E, ?>> extractors = new ArrayList<>();

		private Prepared(List<E> entities)
		{
			this.entities = entities;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param column the column to be added
		 * @param extractor the extractor associated
		 *
		 * @return the same builder with the added column
		 */
		public Prepared<E> set(String column, Function<E, Object> extractor)
		{
			columns.add(column + " = ?");
			extractors.add(extractor);
			return this;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
		 *
		 * @param <T> type of the column added
		 * @param column the column to be added
		 * @param type type of the column to be added
		 * @param extractor the extractor associated
		 *
		 * @return the same builder with the added column
		 */
		public <T> Prepared<E> set(Class<T> type, String column, Function<E, T> extractor)
		{
			extractors.add(extractor);
			Converter.getConverter(type)
				.getColumns(column)
				.map(e -> e + " = ?")
				.forEach(columns::add);
			return this;
		}

		/**
		 * Binds a condition to the update statement
		 *
		 * @param condition to be bound to the update statement
		 *
		 * @return A SQLBuilder with the conditions specified
		 */
		public Sentence.Prepared.Compiled.Builder<E> where(ConstantCondition condition)
		{
			return () -> Sentence.of(Prepared.this + " where " + condition).entities(entities).parameters(extractors);
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public Sentence.Prepared.Compiled.Builder<E> where(ExtractorCondition<E> condition)
		{
			return () -> Sentence.of(Prepared.this + " where " + condition)
				.entities(entities)
				.parameters(Stream.concat(extractors.stream(), condition.getParameters().map(e -> (Function<E, ?>) e))
					.collect(Collectors.toList()));
		}

		@Override
		public Sentence.Prepared.Compiled<E> build()
		{
			return Sentence.of(toString()).entities(entities).parameters(extractors);
		}

		@Override
		public String toString()
		{
			return TableUpdate.this + " set " + columns;
		}

		public class When
		{

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 * @param column the column to be added
			 * @param extractor the extractor associated
			 *
			 * @return the same builder with the added column
			 */
			public Prepared<E> set(String column, Function<E, Object> extractor)
			{
				return Prepared.this.set(column, extractor);
			}

			/**
			 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
			 *
			 * @param <T> type of the column added
			 * @param column the column to be added
			 * @param type type of the column to be added
			 * @param extractor the extractor associated
			 *
			 * @return the same builder with the added column
			 */
			public <T> Prepared<E> set(Class<T> type, String column, Function<E, T> extractor)
			{
				return Prepared.this.set(type, column, extractor);
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
				return Prepared.this.toString();
			}
		}

		public class DisabledWhen extends When
		{

			@Override
			public Prepared<E> set(String column, Function<E, Object> extractor)
			{
				return Prepared.this;
			}

			@Override
			public <T> Prepared<E> set(Class<T> type, String column, Function<E, T> extractor)
			{
				return Prepared.this;
			}

			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	public class When
	{

		/**
		 * Adds a new column to be updated.
		 *
		 * @param column the column to be updated
		 *
		 * @return the same update sentence builder with the added column
		 */
		public Generic set(String column)
		{
			return new Generic().set(column);
		}

		/**
		 * Adds a new column to be updated.
		 *
		 * @param <T> type of the column to be updated
		 * @param type type of the column to be updated
		 * @param column the column to be updated
		 *
		 * @return the same update sentence builder with the added column
		 */
		public <T> Generic set(Class<T> type, String column)
		{
			return new Generic().set(type, column);
		}

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
			return new Compiled().set(column, value);
		}

		/**
		 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
		 *
		 * @param column the column to be added
		 * @param supplier the supplier of the value associated
		 *
		 * @return the same builder with the added column
		 */
		public Compiled setIfTrue(String column, Supplier<Object> supplier)
		{
			return new Compiled().set(column, supplier.get());
		}

		/**
		 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
		 *
		 * @param <T> type of the column added
		 * @param column the column to be added
		 * @param type type of the column to be added
		 * @param value the value associated
		 *
		 * @return the same builder with the added column
		 */
		public <T> Compiled set(Class<T> type, String column, T value)
		{
			return new Compiled().set(type, column, value);
		}

		/**
		 * Adds a new column and it's associated value to the builder if the previous specified condition was true.
		 *
		 * @param <T> type of the column added
		 * @param column the column to be added
		 * @param type type of the column to be added
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
			return TableUpdate.this.toString();
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
