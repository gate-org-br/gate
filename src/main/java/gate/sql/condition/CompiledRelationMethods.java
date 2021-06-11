package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

interface CompiledRelationMethods extends Clause
{

	/**
	 * Adds a new predicate with the specified expression and parameters to the current condition.
	 *
	 * @param expression the expression to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	CompiledPredicate expression(String expression, Object... parameters);

	/**
	 * Evaluates to the result of the specified sub condition.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default CompiledCondition condition(CompiledCondition condition)
	{
		return new CompiledCondition(this)
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
	default CompiledPredicate subquery(Query.Compiled subquery)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + subquery + ")";
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
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
	default CompiledPredicate subquery(Query.Compiled.Builder subquery)
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
	default CompiledPredicate not(Query.Compiled subquery)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + " not (" + subquery + ")";
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
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
	default CompiledPredicate not(Query.Compiled.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/**
	 * Evaluates to true if the specified sub query is not empty.
	 *
	 * @param subquery the sub query to be checked for emptiness
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default CompiledCondition exists(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "exists (" + subquery + ")";
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
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
	default CompiledCondition exists(Query.Compiled.Builder subquery)
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
	CompiledCondition not(CompiledCondition condition);

	interface Rollback extends CompiledRelationMethods
	{

		@Override
		default CompiledCondition condition(CompiledCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition not(CompiledCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition exists(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition exists(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledPredicate not(Query.Compiled.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		default CompiledPredicate not(Query.Compiled subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		default CompiledPredicate subquery(Query.Compiled.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		default CompiledPredicate subquery(Query.Compiled subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		default CompiledPredicate expression(String expression, Object... parameters)
		{
			return new CompiledPredicate.Rollback(getClause());
		}
	}
}
