package gate.sql.update;

import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.Objects;

/**
 * Update builder bound to a source object.
 * <p>
 * Column values are extracted from the source object using property references.
 */
public class ObjectUpdate<T> implements Update
{

	private final T object;
	private final ClassUpdate<T> update;

	ObjectUpdate(Class<T> type, T object)
	{
		this.object = Objects.requireNonNull(object);
		this.update = new ClassUpdate<>(type);
	}

	/**
	 * Adds a new column/value pair by reading the given property from the bound object.
	 * Regular properties map to their corresponding column names and use the property
	 * value directly. Entity-reference properties follow the Gate foreign-key naming
	 * convention, mapping to a column named with the capitalized property name followed
	 * by {@code $} and the referenced entity identifier property. For example,
	 * {@code role} maps to {@code Role$id} and {@code masterRole} maps to
	 * {@code MasterRole$id}. The bound value is the referenced entity identifier.
	 *
	 * @param property property reference used to resolve the column name and extract the value
	 * @param <R>      property type
	 * @return compiled builder with the added column/value pair
	 */
	public <R> Compiled set(PropertyReference<T, R> property)
	{
		return compiled().set(property);
	}

	/**
	 * Adds multiple column/value pairs by reading the given properties from the bound object.
	 * Regular properties map to their corresponding column names and use their values
	 * directly. Entity-reference properties follow the Gate foreign-key naming
	 * convention, mapping to columns named with the capitalized property name followed
	 * by {@code $} and the referenced entity identifier property. For example,
	 * {@code role} maps to {@code Role$id} and {@code masterRole} maps to
	 * {@code MasterRole$id}. The bound values are the referenced entity identifiers.
	 *
	 * @param properties property references used to resolve column names and extract values
	 * @return compiled builder with the added column/value pairs
	 */
	@SafeVarargs
	public final Compiled set(PropertyReference<T, ?>... properties)
	{
		return compiled().set(properties);
	}

	/**
	 * Creates a new compiled builder for this object update.
	 *
	 * @return compiled builder bound to this source object
	 */
	public Compiled compiled()
	{
		return new Compiled();
	}

	/**
	 * Adds the next column(s) only if previous specified condition is true.
	 *
	 * @param assertion condition to be checked
	 * @return conditional builder
	 */
	public When when(boolean assertion)
	{
		return assertion ? new When() : new DisabledWhen();
	}

	/**
	 * Compiled object update builder.
	 */
	public class Compiled implements Sentence.Compiled.Builder
	{

		private final ClassUpdate<T>.Compiled delegate;

		private Compiled()
		{
			this.delegate = update.compiled();
		}

		/**
		 * Adds a new column/value pair, extracting value from the bound object.
		 *
		 * @param property property reference used to resolve column and value
		 * @param <R>      property type
		 * @return the same builder with the added column/value pair
		 */
		public <R> Compiled set(PropertyReference<T, R> property)
		{
			PropertyReference<T, R> reference = Objects.requireNonNull(property);
			delegate.set(reference, reference.apply(object));
			return this;
		}

		/**
		 * Adds new column/value pairs, extracting values from the bound object.
		 *
		 * @param properties property references used to resolve columns and values
		 * @return the same builder with the added column/value pairs
		 */
		@SafeVarargs
		@SuppressWarnings({"rawtypes", "unchecked"})
		public final Compiled set(PropertyReference<T, ?>... properties)
		{
			for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
			{
				PropertyReference<T, Object> reference = (PropertyReference) property;
				delegate.set(reference, reference.apply(object));
			}
			return this;
		}

		public ClassUpdate<T>.Compiled.CompiledWhere where(ConstantCondition condition)
		{
			return delegate.where(condition);
		}

		/**
		 * Adds a compiled condition to this update sentence.
		 *
		 * @param condition condition to be added to the builder
		 * @return the same builder with the added condition
		 */
		public ClassUpdate<T>.Compiled.CompiledWhere where(CompiledCondition condition)
		{
			return delegate.where(condition);
		}

		/**
		 * Adds the next column(s) only if previous specified condition is true.
		 *
		 * @param assertion condition to be checked
		 * @return conditional builder
		 */
		public When when(boolean assertion)
		{
			return assertion ? new When() : new DisabledWhen();
		}


		@Override
		public Sentence.Compiled build()
		{
			return delegate.build();
		}

		@Override
		public String toString()
		{
			return delegate.toString();
		}


		/**
		 * Conditional helper for compiled object update builders.
		 */
		public class When
		{
			/**
			 * Adds a new column/value pair if previous specified condition was true.
			 *
			 * @param property property reference used to resolve column and value
			 * @param <R>      property type
			 * @return the same builder with the added column/value pair
			 */
			public <R> Compiled set(PropertyReference<T, R> property)
			{
				return Compiled.this.set(property);
			}

			/**
			 * Adds new column/value pairs if previous specified condition was true.
			 *
			 * @param properties property references used to resolve columns and values
			 * @return the same builder with the added column/value pairs
			 */
			@SuppressWarnings({"varargs", "unchecked"})
			public Compiled set(PropertyReference<T, ?>... properties)
			{
				return Compiled.this.set(properties);
			}

			/**
			 * Adds the next column(s) only if previous specified condition is true.
			 *
			 * @param assertion condition to be checked
			 * @return conditional builder
			 */
			public When when(boolean assertion)
			{
				return assertion ? this : new DisabledWhen();
			}
		}

		/**
		 * Conditional helper for ignored branches on compiled object update builders.
		 */
		public class DisabledWhen extends When
		{
			/**
			 * Ignores the specified column/value pair and keeps current builder unchanged.
			 *
			 * @param property property reference used to resolve column and value
			 * @param <R>      property type
			 * @return the current compiled builder unchanged
			 */
			@Override
			public <R> Compiled set(PropertyReference<T, R> property)
			{
				return Compiled.this;
			}

			/**
			 * Ignores the specified column/value pairs and keeps current builder unchanged.
			 *
			 * @param properties property references used to resolve columns and values
			 * @return the current compiled builder unchanged
			 */
			@Override
			@SuppressWarnings({"varargs", "unchecked"})
			public Compiled set(PropertyReference<T, ?>... properties)
			{
				return Compiled.this;
			}

			/**
			 * Keeps this conditional helper disabled for subsequent operations.
			 *
			 * @param assertion condition to be checked
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
	 * Conditional helper for object update builders.
	 */
	public class When
	{
		/**
		 * Adds a new column/value pair if previous specified condition was true.
		 *
		 * @param property property reference used to resolve column and value
		 * @param <R>      property type
		 * @return compiled builder with the added column/value pair
		 */
		public <R> Compiled set(PropertyReference<T, R> property)
		{
			return compiled().set(property);
		}

		/**
		 * Adds new column/value pairs if previous specified condition was true.
		 *
		 * @param properties property references used to resolve columns and values
		 * @return compiled builder with the added column/value pairs
		 */
		@SuppressWarnings({"varargs", "unchecked"})
		public Compiled set(PropertyReference<T, ?>... properties)
		{
			return compiled().set(properties);
		}

		/**
		 * Adds the next column(s) only if previous specified condition is true.
		 *
		 * @param assertion condition to be checked
		 * @return conditional builder
		 */
		public When when(boolean assertion)
		{
			return assertion ? this : new DisabledWhen();
		}
	}

	/**
	 * Conditional helper for ignored branches on object update builders.
	 */
	public class DisabledWhen extends When
	{
		/**
		 * Ignores the specified column/value pair and returns an empty compiled builder.
		 *
		 * @param property property reference used to resolve column and value
		 * @param <R>      property type
		 * @return an empty compiled builder
		 */
		@Override
		public <R> Compiled set(PropertyReference<T, R> property)
		{
			return compiled();
		}

		/**
		 * Ignores the specified column/value pairs and returns an empty compiled builder.
		 *
		 * @param properties property references used to resolve columns and values
		 * @return an empty compiled builder
		 */
		@Override
		@SuppressWarnings({"varargs", "unchecked"})
		public Compiled set(PropertyReference<T, ?>... properties)
		{
			return compiled();
		}

		/**
		 * Keeps this conditional helper disabled for subsequent operations.
		 *
		 * @param assertion condition to be checked
		 * @return this disabled conditional helper
		 */
		@Override
		public When when(boolean assertion)
		{
			return this;
		}
	}
}