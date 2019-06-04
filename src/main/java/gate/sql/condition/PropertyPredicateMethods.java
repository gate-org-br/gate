package gate.sql.condition;

import gate.lang.property.Property;
import gate.sql.Clause;
import java.util.Objects;
import java.util.stream.Stream;

interface PropertyPredicateMethods extends Clause
{

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Eq
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is equals to the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isEq(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " = ?";
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ne
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is not equals to the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isNe(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " <> ?";
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is less than the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isLt(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " < ?";
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ge
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is less than or equals the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isLe(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " <= ?";
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is greater than the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isGt(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " > ?";
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ge
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is greater than or equals the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isGe(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " >= ?";
			}
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Lk
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified property value and evaluates to true if the
	 * clause is like the property value.
	 *
	 * @param property property to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default PropertyCondition isLk(Property property)
	{
		Objects.requireNonNull(property, "Attempt to compile a null property into a condition");

		return new PropertyCondition(this)
		{
			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					Stream.of(property));
			}

			@Override
			public String toString()
			{
				return getClause() + " like ?";
			}
		};
	}

	interface Rollback extends PropertyPredicateMethods
	{

		@Override
		default PropertyCondition isEq(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		default PropertyCondition isNe(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		default PropertyCondition isLt(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		default PropertyCondition isLe(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		default PropertyCondition isGt(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		default PropertyCondition isGe(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		default PropertyCondition isLk(Property property)
		{
			return new PropertyCondition(getClause().rollback());
		}
	}

}
