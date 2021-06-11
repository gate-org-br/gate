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
}
