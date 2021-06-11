package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

interface GenericRelationMethods extends Clause
{

	/**
	 * Evaluates to the result of the specified sub condition.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default GenericCondition condition(GenericCondition condition)
	{
		return new GenericCondition(this)
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
		};
	}

	/**
	 * Adds a new predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	default GenericPredicate subquery(Query subquery)
	{
		return new GenericPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + subquery + ")";
			}
		};
	}

	/**
	 * Adds a new predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	default GenericPredicate subquery(Query.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/**
	 * Adds a new negated predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new negated predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	default GenericPredicate not(Query subquery)
	{
		return new GenericPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + " not (" + subquery + ")";
			}
		};
	}

	/**
	 * Adds a new negated predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new negated predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	default GenericPredicate not(Query.Builder subquery)
	{
		return not(subquery.build());
	}

	/**
	 * Evaluates to true if the specified sub query is not empty.
	 *
	 * @param subquery the sub query to be checked for emptiness
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default GenericCondition exists(Query subquery)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "exists (" + subquery + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the specified sub query is not empty.
	 *
	 * @param subquery the sub query to be checked for emptiness
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default GenericCondition exists(Query.Builder subquery)
	{
		return exists(subquery.build());
	}

	/**
	 * Evaluates to true if the specified sub condition is false.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	GenericCondition not(GenericCondition condition);

	interface Rollback extends GenericRelationMethods
	{

		@Override
		default GenericCondition condition(GenericCondition condition)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition exists(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition exists(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericPredicate not(Query.Builder subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		default GenericPredicate not(Query subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		default GenericPredicate subquery(Query.Builder subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		default GenericPredicate subquery(Query subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		default GenericCondition not(GenericCondition condition)
		{
			return new GenericCondition(getClause().rollback());
		}
	}
}
