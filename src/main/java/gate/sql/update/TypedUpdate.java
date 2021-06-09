package gate.sql.update;

import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.PropertyCondition;
import gate.sql.statement.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Update sentence builder for a java type.
 *
 * 
 */
public class TypedUpdate<T> implements Update, Operation.Builder<T>
{

	private final Class<T> type;

	TypedUpdate(Class<T> type)
	{
		Objects.requireNonNull(type);
		Entity.check(type);

		this.type = type;
	}

	/**
	 * Adds a property to be updated.
	 *
	 * @param property the new property to be updated
	 *
	 * @return the same builder with the added property
	 */
	public Generic set(Property property)
	{
		return new Generic().set(property);
	}

	/**
	 * Adds properties to be updated.
	 *
	 * @param properties the new properties to be updated
	 *
	 * @return the same builder with the added properties
	 */
	public Generic set(List<Property> properties)
	{
		return new Generic().set(properties);
	}

	/**
	 * Adds a property to be updated.
	 *
	 * @param property the new property to be updated
	 *
	 * @return the same builder with the added property
	 */
	public Generic set(String property)
	{
		return new Generic().set(property);
	}

	/**
	 * Adds properties to be updated.
	 *
	 * @param properties the new properties to be updated
	 *
	 * @return the same builder with the added properties
	 */
	public Generic set(String... properties)
	{
		return new Generic().set(properties);
	}

	/**
	 * Adds a condition to the builder.
	 *
	 * @param condition condition to be added to the builder
	 *
	 * @return the same builder with the added condition
	 */
	public Generic.ConstantWhere where(ConstantCondition condition)
	{
		return set(Entity.getProperties(type, (e) -> !e.isEntityId())).where(condition);
	}

	/**
	 * Adds a condition to the builder.
	 *
	 * @param condition condition to be added to the builder
	 *
	 * @return the same builder with the added condition
	 */
	public Generic.GenericWhere where(PropertyCondition condition)
	{
		return set(Entity.getProperties(type, (e) -> !e.isEntityId())).where(condition);
	}

	/**
	 * Creates a SQL sentence that updates all properties based on the id.
	 *
	 * @return a SQL sentence that updates all properties based on the id
	 */
	@Override
	public Operation<T> build()
	{
		Property property = Property.getProperty(type, Entity.getId(type));
		return where(Condition.of(property.getColumnName()).isEq(property)).build();
	}

	@Override
	public String toString()
	{
		return "update " + Entity.getFullTableName(type);
	}

	/**
	 * SQLBuilder associated with a java object and a list of properties.
	 *
	 * @see Update
	 */
	public class Generic implements Operation.Builder<T>
	{

		private final StringJoiner columns = new StringJoiner(", ");
		private final List<Property> properties = new ArrayList<>();

		private Generic()
		{
		}

		/**
		 * Adds a property to be updated.
		 *
		 * @param property the new property to be updated
		 *
		 * @return the same instance with the added property
		 */
		public Generic set(Property property)
		{
			properties.add(property);
			property.getConverter()
				.getColumns(property.getColumnName())
				.map(e -> e + " = ?")
				.forEach(columns::add);
			return this;
		}

		/**
		 * Adds properties to be updated.
		 *
		 * @param properties the new properties to be updated
		 *
		 * @return the same instance with the added properties
		 */
		public Generic set(List<Property> properties)
		{
			properties.forEach(this::set);
			return this;
		}

		/**
		 * Adds a property to be updated.
		 *
		 * @param property the new property to be updated
		 *
		 * @return the same instance with the added property
		 */
		public Generic set(String property)
		{
			return set(Property.getProperty(type, property));
		}

		/**
		 * Adds properties to be updated.
		 *
		 * @param properties the new properties to be updated
		 *
		 * @return the same instance with the added properties
		 */
		public Generic set(String... properties)
		{
			return set(Property.getProperties(type, properties));
		}

		/**
		 * Adds a condition to the builder.
		 *
		 * @param condition condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public ConstantWhere where(ConstantCondition condition)
		{
			/**
			 * Creates the SQL sentence with the properties and
			 * conditions specified.
			 *
			 * @return the SQL statement with the properties and
			 * conditions specified
			 */
			return new ConstantWhere(condition);
		}

		/**
		 * Adds a condition to the builder.
		 *
		 * @param condition condition to be added to the builder
		 *
		 * @return the same builder with the added condition
		 */
		public GenericWhere where(PropertyCondition condition)
		{
			return new GenericWhere(condition);
		}

		/**
		 * Creates an update sentence for the specified properties
		 * matching the id.
		 *
		 * @return an update sentence for the specified properties
		 * matching the id
		 */
		@Override
		public Operation<T> build()
		{
			Property property = Property.getProperty(type, Entity.getId(type));
			return where(Condition.of(property.getColumnName()).isEq(property)).build();
		}

		@Override
		public String toString()
		{
			return TypedUpdate.this + " set " + columns;
		}

		public LimittedGeneric limit(int limit)
		{
			return new LimittedGeneric(limit);
		}

		public class LimittedGeneric implements Operation.Builder<T>
		{

			private final int limit;

			private LimittedGeneric(int limit)
			{
				this.limit = limit;
			}

			/**
			 * Creates an update sentence for the specified
			 * properties, conditions and limit.
			 *
			 * @return an update sentence for the properties and
			 * conditions specified
			 */
			@Override
			public Operation<T> build()
			{
				return Operation.of(type, properties, toString());
			}

			@Override
			public String toString()
			{
				return Generic.this + " limit " + limit;
			}
		}

		public class ConstantWhere implements Operation.Builder<T>
		{

			private final ConstantCondition condition;

			private ConstantWhere(ConstantCondition condition)
			{
				this.condition = condition;
			}

			/**
			 * Creates an update sentence for the properties and
			 * conditions specified.
			 *
			 * @return an update sentence for the properties and
			 * conditions specified
			 */
			@Override
			public Operation<T> build()
			{
				return Operation.of(type, properties, toString());
			}

			@Override
			public String toString()
			{
				return Generic.this + " where " + condition;
			}

			public LimittedConstantWhere limit(int limit)
			{
				return new LimittedConstantWhere(limit);
			}

			public class LimittedConstantWhere implements Operation.Builder<T>
			{

				private final int limit;

				private LimittedConstantWhere(int limit)
				{
					this.limit = limit;
				}

				/**
				 * Creates an update sentence for the specified
				 * properties, conditions and limit.
				 *
				 * @return an update sentence for the properties
				 * and conditions specified
				 */
				@Override
				public Operation<T> build()
				{
					return Operation.of(type, properties, toString());
				}

				@Override
				public String toString()
				{
					return ConstantWhere.this + " limit " + limit;
				}
			}
		}

		public class GenericWhere implements Operation.Builder<T>
		{

			private final PropertyCondition condition;

			private GenericWhere(PropertyCondition condition)
			{
				this.condition = condition;
			}

			/**
			 * Creates an update sentence for the properties and
			 * conditions specified.
			 *
			 * @return an update sentence for the properties and
			 * conditions specified
			 */
			@Override
			public Operation<T> build()
			{
				return Operation.of(type,
					Stream.concat(properties.stream(),
						condition.getProperties())
						.collect(Collectors.toList()),
					toString());
			}

			@Override
			public String toString()
			{
				return Generic.this + " where " + condition;
			}

			public LimittedGenericWhere limit(int limit)
			{
				return new LimittedGenericWhere(limit);
			}

			public class LimittedGenericWhere implements Operation.Builder<T>
			{

				private final int limit;

				private LimittedGenericWhere(int limit)
				{
					this.limit = limit;
				}

				/**
				 * Creates an update sentence for the specified
				 * properties, conditions and limit.
				 *
				 * @return an update sentence for the properties
				 * and conditions specified
				 */
				@Override
				public Operation<T> build()
				{
					return Operation.of(type, properties, toString());
				}

				@Override
				public String toString()
				{
					return GenericWhere.this + " limit " + limit;
				}
			}
		}
	}
}
