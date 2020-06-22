package gate.sql.condition;

import gate.lang.property.Property;
import gate.sql.Clause;
import java.util.stream.Stream;

interface PropertyRelationMethods extends Clause
{

	/**
	 * Evaluates to the result of the specified sub condition.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default PropertyCondition condition(PropertyCondition condition)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + condition + ")";
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					condition.getParameters());
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.concat(getClause().getProperties(),
					condition.getProperties());
			}
		};
	}

	/**
	 * Evaluates to true if the specified sub condition is false.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	PropertyCondition not(PropertyCondition condition);

	interface Rollback extends PropertyRelationMethods
	{

		@Override
		default PropertyCondition condition(PropertyCondition condition)
		{
			return new PropertyCondition(getClause().rollback());
		}

	}
}
