package gate.sql.update;

import gate.sql.ColumnReference;
import gate.sql.EntityHelper;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an update statement bound to a table name.
 */
public class ClassUpdate<T> implements Update
{

	private final Class<T> type;

	ClassUpdate(Class<T> type)
	{
		Objects.requireNonNull(type);
		EntityHelper.check(type);
		this.type = type;
	}

	/**
	 * Creates a new SQL update sentence builder bound to a source object.
	 *
	 * @param object source object used to extract values from property references
	 * @return the new object update builder created
	 */
	public ObjectUpdate<T> from(T object)
	{
		return new ObjectUpdate<>(type, Objects.requireNonNull(object));
	}

	/**
	 * Adds a new column to be updated, resolving the target column from a getter
	 * method reference or record accessor.
	 *
	 * @param property property reference used to resolve the target column
	 * @return the same update sentence builder with the added column
	 * @throws NullPointerException  if {@code property} is {@code null}
	 * @throws IllegalStateException if the reference is not a supported method reference
	 */
	public Generic set(PropertyReference<T, ?> property)
	{
		return new Generic().set(Objects.requireNonNull(property));
	}

	/**
	 * Adds new columns to be updated, resolving each target column from getter
	 * method references or record accessors.
	 *
	 * @param properties property references used to resolve target columns
	 * @return the same update sentence builder with the added columns
	 */
	@SafeVarargs
	public final Generic set(PropertyReference<T, ?>... properties)
	{
		return new Generic().set(properties);
	}

	/**
	 * Adds a new column to be updated with the specified value, resolving the
	 * target column from a getter method reference or record accessor.
	 * <p>
	 * Examples:
	 * {@code set(Person::getName, "Alice")} and
	 * {@code set(PersonRecord::name, "Alice")}.
	 *
	 * @param property property reference used to resolve the target column
	 * @param value    the new value of the column
	 * @return the same update sentence builder with the added column
	 * @throws NullPointerException  if {@code property} is {@code null}
	 * @throws IllegalStateException if the reference is not a supported method reference
	 */
	public <R> Compiled set(PropertyReference<T, R> property, R value)
	{
		return new Compiled().set(Objects.requireNonNull(property), value);
	}

	/**
	 * Adds new columns and associated values to the builder extracting values
	 * from the specified object.
	 *
	 * @param object     source object from where values are extracted
	 * @param properties property references used to resolve columns and extract values
	 * @return the same update sentence builder with the added columns and values
	 */
	@SafeVarargs
	public final Compiled set(T object, PropertyReference<T, ?>... properties)
	{
		return new Compiled().set(object, properties);
	}

	/**
	 * Adds the next column to the builder if previous specified condition is true.
	 *
	 * @param assertion the condition to be checked
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
		return "update " + EntityHelper.getFullTableName(type);
	}

	/**
	 * Represents an update statement bound to a table name and column names.
	 */
	public class Generic implements Sentence.Builder
	{

		private final StringJoiner columns = new StringJoiner(", ");

		private Generic()
		{
		}

		/**
		 * Adds a new column to be updated, resolving the target column from a
		 * getter method reference or record accessor.
		 *
		 * @param reference property reference used to resolve the target column
		 * @return the same update sentence builder with the added column
		 * @throws NullPointerException  if {@code property} is {@code null}
		 * @throws IllegalStateException if the reference is not a supported method reference
		 */
		public Generic set(PropertyReference<T, ?> reference)
		{
			columns.add(ColumnReference.of(reference) + " = ?");
			return this;
		}

		/**
		 * Adds new columns to the builder, resolving each target column from
		 * getter method references or record accessors.
		 *
		 * @param properties property references used to resolve target columns
		 * @return the same builder with the added columns
		 */
		@SafeVarargs
		public final Generic set(PropertyReference<T, ?>... properties)
		{
			for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
				set(Objects.requireNonNull(property));
			return this;
		}

		/**
		 * Adds a condition to the builder
		 *
		 * @param condition to be added to the builder
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
		 * @return the same builder with the added condition
		 */
		public GenericWhere where(GenericCondition condition)
		{
			return new GenericWhere(condition);
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
			return ClassUpdate.this + " set " + columns;
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
			 * @param property property reference used to resolve the target column
			 * @return the same update sentence builder with the added column
			 */
			public Generic set(PropertyReference<T, ?> property)
			{
				return Generic.this.set(property);
			}

			/**
			 * Adds new columns to be updated.
			 *
			 * @param properties property references used to resolve target columns
			 * @return the same update sentence builder with the added columns
			 */
			@SuppressWarnings({"varargs", "unchecked"})
			public Generic set(PropertyReference<T, ?>... properties)
			{
				return Generic.this.set(properties);
			}

			/**
			 * Adds the next column to the builder if previous specified condition is true.
			 *
			 * @param assertion the condition to be checked
			 * @return the same builder with the applied condition
			 */
			public When when(boolean assertion)
			{
				return assertion ? this : new DisabledWhen();
			}

			@Override
			public String toString()
			{
				return ClassUpdate.this.toString();
			}
		}

		public class DisabledWhen extends When
		{
			/**
			 * Ignores the specified property and keeps current builder unchanged.
			 *
			 * @param property property reference used to resolve the target column
			 * @return the current generic builder unchanged
			 */
			@Override
			public Generic set(PropertyReference<T, ?> property)
			{
				return Generic.this;
			}

			/**
			 * Ignores the specified properties and keeps current builder unchanged.
			 *
			 * @param properties property references used to resolve target columns
			 * @return the current generic builder unchanged
			 */
			@Override
			@SuppressWarnings({"varargs", "unchecked"})
			public Generic set(PropertyReference<T, ?>... properties)
			{
				return Generic.this;
			}

			/**
			 * Keeps this conditional helper disabled for subsequent operations.
			 *
			 * @param assertion the condition to be checked
			 * @return this disabled conditional helper
			 */
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
		 * Adds a new column, and it's associated value to the builder, resolving
		 * the target column from a getter method reference or record accessor.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    the value associated
		 * @param <R>      property type
		 * @return the same builder with the added column
		 */
		public <R> Compiled set(PropertyReference<T, R> property, R value)
		{
			columns.add(ColumnReference.of(property) + " = ?");
			values.add(property.identity(value));
			return this;
		}

		/**
		 * Adds new columns and associated values to the builder extracting values
		 * from the specified object.
		 *
		 * @param object     source object from where values are extracted
		 * @param properties property references used to resolve columns and extract values
		 * @return the same builder with the added columns and values
		 */
		@SafeVarargs
		public final Compiled set(T object, PropertyReference<T, ?>... properties)
		{
			Objects.requireNonNull(object);
			for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
			{
				Objects.requireNonNull(property);
				columns.add(ColumnReference.of(property) + " = ?");
				values.add(property.apply(object));
			}
			return this;
		}

		/**
		 * Binds a condition to the update statement
		 *
		 * @param condition to be bound to the update statement
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
		 * @return the same builder with the added condition
		 */
		public CompiledWhere where(CompiledCondition condition)
		{
			return new CompiledWhere(condition);
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
		 * @return the same builder with the applied condition
		 */
		public When when(boolean assertion)
		{
			return assertion ? new When() : new DisabledWhen();
		}

		@Override
		public String toString()
		{
			return ClassUpdate.this + " set " + columns;
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
				return Sentence.of(toString()).parameters(Stream.concat(values.stream(), condition.getParameters()).toList());
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
					return Sentence.of(toString()).parameters(Stream.concat(values.stream(), condition.getParameters()).collect(Collectors.toList()));
				}
			}
		}

		public class When
		{
			/**
			 * Adds a new column, and it's associated value to the builder, resolving
			 * the target column from a getter method reference or record accessor.
			 *
			 * @param property property reference used to resolve the target column
			 * @param value    the value associated
			 * @param <R>      property type
			 * @return the same builder with the added column
			 */
			public <R> Compiled set(PropertyReference<T, R> property, R value)
			{
				return Compiled.this.set(property, value);
			}

			/**
			 * Adds new columns and associated values to the builder extracting
			 * values from the specified object if the previous specified condition
			 * was true.
			 *
			 * @param object     source object from where values are extracted
			 * @param properties property references used to resolve columns and extract values
			 * @return the same builder with the added columns and values
			 */
			@SuppressWarnings({"varargs", "unchecked"})
			public Compiled set(T object, PropertyReference<T, ?>... properties)
			{
				return Compiled.this.set(object, properties);
			}

			/**
			 * Adds the next column to the builder if previous specified condition is true.
			 *
			 * @param assertion the condition to be checked
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

			/**
			 * Ignores the specified property/value pair and keeps current builder unchanged.
			 *
			 * @param property property reference used to resolve the target column
			 * @param value    the value associated
			 * @param <R>      property type
			 * @return the current compiled builder unchanged
			 */
			@Override
			public <R> Compiled set(PropertyReference<T, R> property, R value)
			{
				return Compiled.this;
			}

			/**
			 * Ignores the specified properties and keeps current builder unchanged.
			 *
			 * @param object     source object from where values are extracted
			 * @param properties property references used to resolve columns and extract values
			 * @return the current compiled builder unchanged
			 */
			@Override
			@SuppressWarnings({"varargs", "unchecked"})
			public Compiled set(T object, PropertyReference<T, ?>... properties)
			{
				return Compiled.this;
			}

			/**
			 * Keeps this conditional helper disabled for subsequent operations.
			 *
			 * @param assertion the condition to be checked
			 * @return this disabled conditional helper
			 */
			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	/**
	 * Conditional helper for class update builders.
	 */
	public class When
	{
		/**
		 * Adds a new column to be updated if previous specified condition was true.
		 *
		 * @param property property reference used to resolve the target column
		 * @return the same update sentence builder with the added column
		 */
		@SuppressWarnings({"varargs"})
		public Generic set(PropertyReference<T, ?> property)
		{
			return new Generic().set(property);
		}

		/**
		 * Adds new columns to be updated.
		 *
		 * @param properties property references used to resolve target columns
		 * @return the same update sentence builder with the added columns
		 */
		@SuppressWarnings({"varargs", "unchecked"})
		public Generic set(PropertyReference<T, ?>... properties)
		{
			return new Generic().set(properties);
		}

		/**
		 * Adds a new column, and it's associated value to the builder if the
		 * previous specified condition was true.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    the value associated
		 * @param <R>      property type
		 * @return the same builder with the added column
		 */
		public <R> Compiled set(PropertyReference<T, R> property, R value)
		{
			return new Compiled().set(property, value);
		}

		/**
		 * Adds new columns and associated values to the builder extracting values
		 * from the specified object if the previous specified condition was true.
		 *
		 * @param object     source object from where values are extracted
		 * @param properties property references used to resolve columns and extract values
		 * @return the same builder with the added columns and values
		 */
		@SuppressWarnings({"varargs", "unchecked"})
		public Compiled set(T object, PropertyReference<T, ?>... properties)
		{
			return new Compiled().set(object, properties);
		}


		/**
		 * Adds the next column to the builder if previous specified condition is true.
		 *
		 * @param assertion the condition to be checked
		 * @return the same builder with the applied condition
		 */
		public When when(boolean assertion)
		{
			return assertion ? this : new DisabledWhen();
		}

		@Override
		public String toString()
		{
			return ClassUpdate.this.toString();
		}
	}

	public class DisabledWhen extends When
	{
		/**
		 * Ignores the specified property and returns an empty generic builder.
		 *
		 * @param property property reference used to resolve the target column
		 * @return an empty generic builder
		 */
		@Override
		@SuppressWarnings({"varargs"})
		public Generic set(PropertyReference<T, ?> property)
		{
			return new Generic();
		}

		/**
		 * Ignores the specified properties and returns an empty generic builder.
		 *
		 * @param properties property references used to resolve target columns
		 * @return an empty generic builder
		 */
		@Override
		@SuppressWarnings({"varargs", "unchecked"})
		public Generic set(PropertyReference<T, ?>... properties)
		{
			return new Generic();
		}

		/**
		 * Ignores the specified property/value pair and returns an empty compiled builder.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    the value associated
		 * @param <R>      property type
		 * @return an empty compiled builder
		 */
		@Override
		public <R> Compiled set(PropertyReference<T, R> property, R value)
		{
			return new Compiled();
		}

		/**
		 * Ignores the specified properties and returns an empty compiled builder.
		 *
		 * @param object     source object from where values are extracted
		 * @param properties property references used to resolve columns and extract values
		 * @return an empty compiled builder
		 */
		@Override
		@SuppressWarnings({"varargs", "unchecked"})
		public Compiled set(T object, PropertyReference<T, ?>... properties)
		{
			return new Compiled();
		}

		/**
		 * Keeps this conditional helper disabled for subsequent operations.
		 *
		 * @param assertion the condition to be checked
		 * @return this disabled conditional helper
		 */
		@Override
		public When when(boolean assertion)
		{
			return this;
		}
	}
}
