package gate.sql.condition;

import gate.sql.Clause;

/**
 * A single predicate of a condition.
 */
public abstract class Predicate implements Clause
{

	private final Clause clause;

	Predicate(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	@Override
	public String toString()
	{
		return getClause().toString();
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
	public abstract Predicate when(boolean assertion);

}
