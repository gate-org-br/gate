package gate.sql.select;

import gate.sql.Clause;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import java.util.stream.Stream;

/**
 * A clause that can be filtered
 */
public interface Filterable extends Clause
{

	/**
	 * Defines the where clause of the SQL statement.
	 *
	 * @param predicate the {@link gate.sql.condition.Predicate} associated with the where clause
	 *
	 * @return the current builder, for chained invocations
	 */
	FilteredSelect where(ConstantCondition predicate);

	/**
	 * A filtered clause without parameters
	 */
	interface Constant extends Filterable
	{

		@Override
		default FilteredSelect.Constant where(ConstantCondition predicate)
		{
			return new FilteredSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}
			};
		}

		/**
		 * Defines the where clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the where clause
		 *
		 * @return the current builder, for chained invocations
		 */
		default FilteredSelect.Generic where(GenericCondition predicate)
		{
			return new FilteredSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}
			};
		}

		/**
		 * Defines the where clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the where clause
		 *
		 * @return the current builder, for chained invocations
		 */
		default FilteredSelect.Compiled where(CompiledCondition predicate)
		{
			return new FilteredSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(), predicate.getParameters());
				}
			};
		}
	}

	/**
	 * A filtered clause whose parameters are yet to be defined
	 */
	interface Generic extends Filterable
	{

		@Override
		default FilteredSelect.Generic where(ConstantCondition predicate)
		{
			return new FilteredSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}
			};
		}

		/**
		 * Defines the where clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the where clause
		 *
		 * @return the current builder, for chained invocations
		 */
		default FilteredSelect.Generic where(GenericCondition predicate)
		{
			return new FilteredSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}
			};
		}

	}

	/**
	 * A filtered clause whose parameters are already defined
	 */
	interface Compiled extends Filterable
	{

		@Override
		default FilteredSelect.Compiled where(ConstantCondition predicate)
		{
			return new FilteredSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}
			};
		}

		/**
		 * Defines the where clause of the SQL statement.
		 *
		 * @param predicate the {@link gate.sql.condition.Predicate} associated with the where clause
		 *
		 * @return the current builder, for chained invocations
		 */
		default FilteredSelect.Compiled where(CompiledCondition predicate)
		{
			return new FilteredSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " where " + predicate.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(), predicate.getParameters());
				}
			};
		}

	}
}
