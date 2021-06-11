package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

/**
 * A relation between two predicates of a compiled condition
 *
 * @see gate.sql.condition.CompiledCondition
 * @see gate.sql.condition.CompiledPredicate
 */
public class CompiledRelation extends Relation
	implements ConstantRelationMethods, CompiledRelationMethods
{

	CompiledRelation(Clause clause)
	{
		super(clause);
	}

	@Override
	public CompiledRelation when(boolean assertion)
	{
		return assertion ? this : new Rollback(getClause());
	}

	@Override
	public CompiledPredicate expression(String expression)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + expression;
			}
		};
	}

	@Override
	public CompiledPredicate expression(String expression, Object... parameters)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + expression;
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameters));
			}
		};
	}

	@Override
	public CompiledCondition condition(ConstantCondition expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + expression + ")";
			}
		};
	}

	@Override
	public CompiledPredicate subquery(Query.Constant subquery)
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
		};
	}

	@Override
	public CompiledPredicate subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	@Override
	public CompiledCondition exists(Query.Constant subquery)
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

		};
	}

	@Override
	public CompiledCondition exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	@Override
	public CompiledPredicate not(Query.Constant subquery)
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
		};
	}

	@Override
	public CompiledPredicate not(Query.Constant.Builder subquery)
	{
		return not(subquery.build());
	}

	@Override
	public CompiledRelation not()
	{
		return new CompiledRelation(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "not";
			}
		};
	}

	@Override
	public CompiledPredicate not(String expression)
	{
		return not().expression(expression);
	}

	@Override
	public CompiledCondition not(ConstantCondition expression)
	{
		return not().condition(expression);
	}

	@Override
	public CompiledCondition not(CompiledCondition condition)
	{
		return not().condition(condition);
	}

	static class Rollback extends CompiledRelation
		implements CompiledRelationMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public CompiledRelation when(boolean assertion)
		{
			return this;
		}

		@Override
		public CompiledRelation not()
		{
			return this;
		}

		@Override
		public CompiledCondition not(CompiledCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition not(ConstantCondition expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition condition(ConstantCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition exists(Query.Constant.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition exists(Query.Constant subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledPredicate not(String expression)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate not(Query.Constant.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate not(Query.Constant subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate subquery(Query.Constant.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate subquery(Query.Constant subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate expression(String expression)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate expression(String expression, Object... parameters)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledCondition exists(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition exists(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledPredicate subquery(Query.Compiled.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate subquery(Query.Compiled subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate not(Query.Compiled.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate not(Query.Compiled subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledCondition condition(CompiledCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

	}
}
