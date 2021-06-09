package gate.sql.update;

import gate.converter.Converter;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.ExtractorCondition;
import gate.sql.condition.GenericCondition;
import gate.sql.statement.Sentence;
import java.util.ArrayList;
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

	private final String string;

	TableUpdate(String string)
	{
		this.string = string;
	}

	/**
	 * Binds the update statement to a list of entities.
	 *
	 * 
	 * statement
	 * @param type of the entities from where the values are to be extracted
	 *
	 * @return the same builder with the associated entities
	 */
	public <T> Prepared<T> from(Class<T> type)
	{
		return new Prepared<>(type);
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
	 * 
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
	 * 
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
	 * Adds the next column to the builder if previous specified condition
	 * is true.
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
	 * Compiles this update builder
	 *
	 * @return the created compiled builder
	 */
	public Compiled compiled()
	{
		return new Compiled();
	}

	@Override
	public String toString()
	{
		return string;
	}

	/**
	 * Represents a an update statement bound to a table name and column
	 * names.
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
		 * 
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
		public GenericWhere where(ConstantCondition condition)
		{
			return new GenericWhere(condition);
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public GenericWhere where(GenericCondition condition)
		{
			return new GenericWhere(condition);
		}

		/**
		 * Creates a sentence with the specified columns and no
		 * condition associated.
		 *
		 * @return a sentence with the specified columns and no
		 * condition associated
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

		public class GenericWhere implements Sentence.Builder
		{

			private final Condition condition;

			public GenericWhere(ConstantCondition condition)
			{
				this.condition = condition;
			}

			public GenericWhere(GenericCondition condition)
			{
				this.condition = condition;
			}

			@Override
			public Sentence build()
			{
				return Sentence.of(toString());
			}

			@Override
			public String toString()
			{
				return Generic.this + " where " + condition;
			}

			public LimitedGenericWhere limit(int limit)
			{
				return new LimitedGenericWhere(limit);
			}

			public class LimitedGenericWhere implements Sentence.Builder
			{

				private final int limit;

				public LimitedGenericWhere(int limit)
				{
					this.limit = limit;
				}

				@Override
				public String toString()
				{
					return GenericWhere.this + " limit " + limit;
				}

				@Override
				public Sentence build()
				{
					return Sentence.of(toString());
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
			 * @return the same update sentence builder with the
			 * added column
			 */
			public Generic set(String column)
			{
				return Generic.this.set(column);
			}

			/**
			 * Adds a new column to be updated.
			 *
			 * 
			 * @param type type of the column to be updated
			 * @param column the column to be updated
			 *
			 * @return the same update sentence builder with the
			 * added column
			 */
			public <T> Generic set(Class<T> type, String column)
			{
				return Generic.this.set(type, column);
			}

			/**
			 * Adds the next column to the builder if previous
			 * specified condition is true.
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

		private final List<Object> values;
		private final StringJoiner columns = new StringJoiner(", ");

		Compiled()
		{
			this(new ArrayList<>());
		}

		Compiled(List<Object> values)
		{
			this.values = values;
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
			values.add(null);
			return this;
		}

		/**
		 * Adds a new column and it's associated value to the builder.
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
		public CompiledWhere where(ConstantCondition condition)
		{
			return new CompiledWhere(condition);
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public CompiledWhere where(CompiledCondition condition)
		{
			return new CompiledWhere(condition);
		}

		/**
		 * Creates a sentence with the specified columns, values and no
		 * condition associated.
		 *
		 * @return a sentence with the specified columns, values and no
		 * condition associated
		 */
		@Override
		public Sentence.Compiled build()
		{
			return Sentence.of(toString()).parameters(values);
		}

		/**
		 * Adds the next column to the builder if previous specified
		 * condition is true.
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

		public class CompiledWhere implements Sentence.Compiled.Builder
		{

			private final Condition condition;

			public CompiledWhere(ConstantCondition condition)
			{
				this.condition = condition;
			}

			public CompiledWhere(CompiledCondition condition)
			{
				this.condition = condition;
			}

			@Override
			public Sentence.Compiled build()
			{
				return Sentence.of(toString()).parameters(Stream.concat(values.stream(),
					condition.getParameters()).collect(Collectors.toList()));
			}

			@Override
			public String toString()
			{
				return Compiled.this + " where " + condition;
			}

			public LimitedCompiledWhere limit(int limit)
			{
				return new LimitedCompiledWhere(limit);
			}

			public class LimitedCompiledWhere implements Sentence.Compiled.Builder
			{

				private final int limit;

				public LimitedCompiledWhere(int limit)
				{
					this.limit = limit;
				}

				@Override
				public String toString()
				{
					return CompiledWhere.this + " limit " + limit;
				}

				@Override
				public Sentence.Compiled build()
				{
					return Sentence.of(toString()).parameters(Stream.concat(values.stream(),
						condition.getParameters()).collect(Collectors.toList()));
				}
			}
		}

		public class When
		{

			/**
			 * Adds a new column and it's associated value to the
			 * builder if the previous specified condition was true.
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
			 * Adds a new column and it's associated value to the
			 * builder if the previous specified condition was true.
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
			 * Adds a new column and it's associated value to the
			 * builder if the previous specified condition was true.
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
			 * Adds a new column and it's associated value to the
			 * builder if the previous specified condition was true.
			 *
			 * 
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
			 * Adds the next column to the builder if previous
			 * specified condition is true.
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
	 * SQL insert sentence builder for a table with values specified and
	 * ready for execution.
	 *
	 * @param <E> type of the entities to be inserted on database
	 */
	public class Prepared<E> implements Sentence.Extractor.Compiled.Builder
	{

		private final Class<E> type;
		private final StringJoiner columns = new StringJoiner(", ");
		private final List<Function<E, ?>> extractors = new ArrayList<>();

		private Prepared(Class<E> type)
		{
			this.type = type;
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
		 * 
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
		public Sentence.Extractor.Compiled.Builder<E> where(ConstantCondition condition)
		{
			return () -> Sentence.of(Prepared.this + " where " + condition)
				.from(type)
				.parameters(extractors);
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public Sentence.Extractor.Compiled.Builder<E> where(ExtractorCondition<E> condition)
		{
			return () -> Sentence.of(Prepared.this + " where " + condition)
				.from(type)
				.parameters(Stream.concat(extractors.stream(), condition.getParameters().map(e -> (Function<E, ?>) e))
					.collect(Collectors.toList()));
		}

		@Override
		public Sentence.Extractor.Compiled<E> build()
		{
			return Sentence.of(toString()).from(type).parameters(extractors);
		}

		@Override
		public String toString()
		{
			return TableUpdate.this + " set " + columns;
		}

		public class When
		{

			/**
			 * Adds a new column and it's associated value to the
			 * builder if the previous specified condition was true.
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
			 * Adds a new column and it's associated value to the
			 * builder if the previous specified condition was true.
			 *
			 * 
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
			 * Adds the next column to the builder if previous
			 * specified condition is true.
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
		 * @return the same update sentence builder with the added
		 * column
		 */
		public Generic set(String column)
		{
			return new Generic().set(column);
		}

		/**
		 * Adds a new column to be updated.
		 *
		 * 
		 * @param type type of the column to be updated
		 * @param column the column to be updated
		 *
		 * @return the same update sentence builder with the added
		 * column
		 */
		public <T> Generic set(Class<T> type, String column)
		{
			return new Generic().set(type, column);
		}

		/**
		 * Adds a new column and it's associated value to the builder if
		 * the previous specified condition was true.
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
		 * Adds a new column and it's associated value to the builder if
		 * the previous specified condition was true.
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
		 * Adds a new column and it's associated value to the builder if
		 * the previous specified condition was true.
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
			return new Compiled().set(type, column, value);
		}

		/**
		 * Adds a new column and it's associated value to the builder if
		 * the previous specified condition was true.
		 *
		 * 
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
		 * Adds the next column to the builder if previous specified
		 * condition is true.
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
