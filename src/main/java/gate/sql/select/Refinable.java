package gate.sql.select;

import gate.sql.Clause;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import java.util.stream.Stream;

public interface Refinable extends Clause
{

	public interface Constant extends Refinable
	{

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current {@link gate.sql.builder.SQLBuilder}, for chained invocations
		 */
		public RefinedSelect having(ConstantCondition predicate);

	}

	public interface Generic extends Refinable
	{

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current {@link gate.sql.builder.SQLBuilder}, for chained invocations
		 */
		default RefinedSelect.Generic having(GenericCondition predicate)
		{
			return new RefinedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}
			};
		}

	}

	public interface Compiled extends Refinable
	{

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current {@link gate.sql.builder.SQLBuilder}, for chained invocations
		 */
		default RefinedSelect.Compiled having(CompiledCondition predicate)
		{
			return new RefinedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
						predicate.getParameters());
				}
			};
		}

	}
}
