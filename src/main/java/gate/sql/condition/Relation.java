package gate.sql.condition;

import gate.sql.Clause;

/**
 * A relation between two predicates of a condition.
 */
public abstract class Relation implements Clause
{

	private final Clause clause;

	Relation(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	@Override
	public Clause rollback()
	{
		return clause.rollback();
	}

	/**
	 * Evaluates the current predicate only if the specified assertion is true.
	 *
	 * @param assertion the assertion to be tested before the current predicate is evaluated
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Relation when(boolean assertion);
}
