package gate.sql.condition;

import gate.adapter.registry.ColumnReferenceRegistry;
import gate.sql.Clause;
import gate.sql.statement.Query;
import gate.type.PropertyReference;

import java.util.stream.Stream;

/**
 * A relation between two predicates of a compiled condition
 *
 * @see CompiledCondition
 * @see CompiledPredicate
 */
public class LazyCompiledRelation extends Relation
{

	LazyCompiledRelation(Clause clause)
	{
		super(clause);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LazyCompiledRelation when(boolean assertion)
	{
		return assertion ? this : new Rollback(getClause());
	}

	/**
	 * Adds a new predicate with the specified expression.
	 *
	 * @param expression the expression to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate expression(String expression)
	{
		return new LazyCompiledPredicate(this)
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

	/**
	 * Adds a new predicate with the specified property reference.
	 *
	 * @param reference the property reference to be associated with the new predicate
	 * @param <T>       the property owner type
	 * @param <R>       the property return type
	 * @return the new predicate created, for chained invocations
	 */
	public <T, R> LazyCompiledPredicate expression(PropertyReference<T, R> reference)
	{
		return expression(ColumnReferenceRegistry.INSTANCE.get(reference).name());
	}

	/**
	 * Adds a constant sub condition supplied lazily.
	 *
	 * @param supplier supplier of the constant condition
	 * @return the current condition, for chained invocations
	 */
	public CompiledCondition condition(ConstantConditionSupplier supplier)
	{
		var condition = supplier.get();
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
		};
	}

	/**
	 * Adds a compiled sub condition supplied lazily.
	 *
	 * @param supplier supplier of the compiled condition
	 * @return the current condition, for chained invocations
	 */
	public CompiledCondition condition(CompiledConditionSupplier supplier)
	{
		var condition = supplier.get();
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
				return Stream.concat(super.getParameters(), condition.getParameters());
			}
		};
	}

	/**
	 * Adds a constant sub query supplied by a builder.
	 *
	 * @param supplier supplier of the constant query builder
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate subquery(ConstantQueryBuilderSupplier supplier)
	{
		return subquery(() -> supplier.get().build());
	}

	/**
	 * Adds a constant sub query supplied lazily.
	 *
	 * @param supplier supplier of the constant query
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate subquery(ConstantQuerySupplier supplier)
	{
		return new LazyCompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + supplier.get() + ")";
			}
		};
	}

	/**
	 * Adds a compiled sub query supplied by a builder.
	 *
	 * @param supplier supplier of the compiled query builder
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate subquery(CompiledQueryBuilderSupplier supplier)
	{
		return subquery(() -> supplier.get().build());
	}

	/**
	 * Adds a compiled sub query supplied lazily.
	 *
	 * @param supplier supplier of the compiled query
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate subquery(CompiledQuerySupplier supplier)
	{
		var subquery = supplier.get();
		return new LazyCompiledPredicate(this)
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
	 * Adds an EXISTS clause from a constant sub query builder supplier.
	 *
	 * @param supplier supplier of the constant query builder
	 * @return the current condition, for chained invocations
	 */
	public CompiledCondition exists(ConstantQueryBuilderSupplier supplier)
	{
		return exists(() -> supplier.get().build());
	}

	/**
	 * Adds an EXISTS clause from a constant sub query supplier.
	 *
	 * @param supplier supplier of the constant query
	 * @return the current condition, for chained invocations
	 */
	public CompiledCondition exists(ConstantQuerySupplier supplier)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "exists (" + supplier.get() + ")";
			}
		};
	}

	/**
	 * Adds an EXISTS clause from a compiled sub query builder supplier.
	 *
	 * @param supplier supplier of the compiled query builder
	 * @return the current condition, for chained invocations
	 */
	public CompiledCondition exists(CompiledQueryBuilderSupplier supplier)
	{
		return exists(() -> supplier.get().build());
	}

	/**
	 * Adds an EXISTS clause from a compiled sub query supplier.
	 *
	 * @param supplier supplier of the compiled query
	 * @return the current condition, for chained invocations
	 */
	public CompiledCondition exists(CompiledQuerySupplier supplier)
	{
		var subquery = supplier.get();
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
	 * Adds a negated predicate from a constant sub query builder supplier.
	 *
	 * @param supplier supplier of the constant query builder
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate not(ConstantQueryBuilderSupplier supplier)
	{
		return not(() -> supplier.get().build());
	}

	/**
	 * Adds a negated predicate from a constant sub query supplier.
	 *
	 * @param supplier supplier of the constant query
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate not(ConstantQuerySupplier supplier)
	{
		return new LazyCompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + " not (" + supplier.get() + ")";
			}
		};
	}

	/**
	 * Adds a negated predicate from a compiled sub query builder supplier.
	 *
	 * @param supplier supplier of the compiled query builder
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate not(CompiledQueryBuilderSupplier supplier)
	{
		return not(() -> supplier.get().build());
	}

	/**
	 * Adds a negated predicate from a compiled sub query supplier.
	 *
	 * @param supplier supplier of the compiled query
	 * @return the new predicate created, for chained invocations
	 */
	public LazyCompiledPredicate not(CompiledQuerySupplier supplier)
	{
		var subquery = supplier.get();
		return new LazyCompiledPredicate(this)
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
	 * Creates a negated relation.
	 *
	 * @return the current relation, for chained invocations
	 */
	public LazyCompiledRelation not()
	{
		return new LazyCompiledRelation(this)
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

	/**
	 * Adds a negated predicate with the specified expression.
	 *
	 * @param expression the expression to be tested
	 * @return the current predicate, for chained invocations
	 */
	public LazyCompiledPredicate not(String expression)
	{
		return not().expression(expression);
	}

	/**
	 * Adds a new negated predicate from a property reference.
	 *
	 * @param reference the property reference to be associated with the new predicate
	 * @param <T>       the property owner type
	 * @param <R>       the property return type
	 * @return the new predicate created, for chained invocations
	 */
	public <T, R> LazyCompiledPredicate not(PropertyReference<T, R> reference)
	{
		return not().expression(reference);
	}

	static class Rollback extends LazyCompiledRelation
	{
		public Rollback(Clause clause)
		{
			super(clause.rollback());
		}

		@Override
		public LazyCompiledPredicate subquery(ConstantQueryBuilderSupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate subquery(ConstantQuerySupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate not(CompiledQueryBuilderSupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate not(CompiledQuerySupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}


		@Override
		public LazyCompiledRelation when(boolean assertion)
		{
			return this;
		}

		@Override
		public LazyCompiledRelation not()
		{
			return new LazyCompiledRelation.Rollback(getClause());
		}


		@Override
		public CompiledCondition condition(CompiledConditionSupplier condition)
		{
			return new CompiledCondition(getClause());
		}

		@Override
		public CompiledCondition condition(ConstantConditionSupplier condition)
		{
			return new CompiledCondition(getClause());
		}

		@Override
		public CompiledCondition exists(ConstantQueryBuilderSupplier subquery)
		{
			return new CompiledCondition(getClause());
		}

		@Override
		public CompiledCondition exists(ConstantQuerySupplier subquery)
		{
			return new CompiledCondition(getClause());
		}

		@Override
		public CompiledCondition exists(CompiledQueryBuilderSupplier supplier)
		{
			return new CompiledCondition(getClause());
		}

		@Override
		public CompiledCondition exists(CompiledQuerySupplier supplier)
		{
			return new CompiledCondition(getClause());
		}

		@Override
		public LazyCompiledPredicate not(String expression)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public <T, R> LazyCompiledPredicate not(PropertyReference<T, R> reference)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate not(ConstantQueryBuilderSupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate not(ConstantQuerySupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate subquery(CompiledQueryBuilderSupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate subquery(CompiledQuerySupplier supplier)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public LazyCompiledPredicate expression(String expression)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}

		@Override
		public <T, R> LazyCompiledPredicate expression(PropertyReference<T, R> reference)
		{
			return new LazyCompiledPredicate.Rollback(getClause());
		}
	}

	@FunctionalInterface
	public interface ConstantQueryBuilderSupplier
	{
		/**
		 * Gets a constant query builder.
		 *
		 * @return the constant query builder
		 */
		Query.Constant.Builder get();
	}

	@FunctionalInterface
	public interface ConstantQuerySupplier
	{
		/**
		 * Gets a constant query.
		 *
		 * @return the constant query
		 */
		Query.Constant get();
	}

	@FunctionalInterface
	public interface CompiledQueryBuilderSupplier
	{
		/**
		 * Gets a compiled query builder.
		 *
		 * @return the compiled query builder
		 */
		Query.Compiled.Builder get();
	}


	@FunctionalInterface
	public interface CompiledQuerySupplier
	{
		/**
		 * Gets a compiled query.
		 *
		 * @return the compiled query
		 */
		Query.Compiled get();
	}

	@FunctionalInterface
	public interface CompiledConditionSupplier
	{
		/**
		 * Gets a compiled condition.
		 *
		 * @return the compiled condition
		 */
		CompiledCondition get();
	}

	@FunctionalInterface
	public interface ConstantConditionSupplier
	{
		/**
		 * Gets a compiled condition from a constant source.
		 *
		 * @return the compiled condition
		 */
		CompiledCondition get();
	}
}