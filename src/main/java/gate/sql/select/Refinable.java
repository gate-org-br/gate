package gate.sql.select;

import gate.sql.Clause;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import java.util.stream.Stream;

public interface Refinable extends Clause
{

	/**
	 * Defines the having clause of the SQL statement.
	 *
	 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
	 *
	 * @return the current builder, for chained invocations
	 */
	RefinedSelect having(ConstantCondition predicate);

	interface Constant extends Refinable
	{

		default RefinedSelect.Constant having(ConstantCondition predicate)
		{
			return new RefinedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}
			};
		}

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current builder, for chained invocations
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

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current builder, for chained invocations
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

	interface Generic extends Refinable
	{

		@Override
		default RefinedSelect.Generic having(ConstantCondition predicate)
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

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current builder, for chained invocations
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

	interface Compiled extends Refinable
	{

		@Override
		default RefinedSelect.Compiled having(ConstantCondition predicate)
		{
			return new RefinedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}
			};
		}

		/**
		 * Defines the having clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the having clause
		 *
		 * @return the current builder, for chained invocations
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
