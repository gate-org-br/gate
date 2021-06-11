package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.Objects;

interface GenericPredicateMethods extends Clause
{

	/**
	 * Evaluates to true if the previously specified clause is equals to the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition eq()
	{
		return new GenericCondition(this)
		{

			@Override
			public String toString()
			{
				return getClause() + " = ?";
			}
		};
	}

	default GenericCondition isEq(Query subquery)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " = (" + subquery.toString() + ")";
			}
		};
	}

	default GenericCondition isEq(Query.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return isEq(subquery.build());
	}

	/**
	 * Evaluates to true if the previously specified clause is not equals to the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition ne()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> ?";
			}
		};
	}

	default GenericCondition isNe(Query subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> (" + subquery.toString() + ")";
			}
		};
	}

	default GenericCondition isNe(Query.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return GenericPredicateMethods.this.isNe(subquery.build());
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition lt()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < ?";
			}
		};
	}

	default GenericCondition isLt(Query subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < (" + subquery.toString() + ")";
			}
		};
	}

	default GenericCondition isLt(Query.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return GenericPredicateMethods.this.isLt(subquery.build());
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition le()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= ?";
			}
		};
	}

	default GenericCondition isLe(Query subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= (" + subquery.toString() + ")";
			}
		};
	}

	default GenericCondition isLe(Query.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return GenericPredicateMethods.this.isLe(subquery.build());
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition gt()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > ?";
			}
		};
	}

	default GenericCondition isGt(Query subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > (" + subquery.toString() + ")";
			}
		};
	}

	default GenericCondition isGt(Query.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return GenericPredicateMethods.this.isGt(subquery.build());
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals to the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition ge()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= ?";
			}
		};
	}

	default GenericCondition isGe(Query subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= (" + subquery.toString() + ")";
			}
		};
	}

	default GenericCondition isGe(Query.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return GenericPredicateMethods.this.isGe(subquery.build());
	}

	/**
	 * Evaluates to true if the previously specified clause is like the parameter to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition lk()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " like ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause matches the regular expression to be specified on execution.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default GenericCondition rx()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " rlike ?";
			}
		};
	}

	default GenericCondition bw()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " between ? and ?";
			}
		};
	}

	interface Rollback extends GenericPredicateMethods
	{

		@Override
		default GenericCondition bw()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition rx()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition lk()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isGe(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isGe(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition ge()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isGt(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isGt(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition gt()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isLe(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isLe(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition le()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isLt(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isLt(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition lt()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isNe(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isNe(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition ne()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isEq(Query.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition isEq(Query subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		default GenericCondition eq()
		{
			return new GenericCondition(getClause().rollback());
		}
	}
}
