package gate.sql.select;

import gate.sql.Clause;

public interface OfAble extends Clause
{
	/**
	 * Specifies the table to be locked.
	 *
	 * @param table the table to be locked
	 * @return the current builder, for chained invocations
	 */
	OfSelect of(String table);

	interface Constant extends OfAble
	{

		default OfSelect.Constant of(String table)
		{
			return new OfSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " of " + table;
				}
			};
		}
	}

	interface Generic extends OfAble
	{
		default OfSelect.Generic of(String table)
		{
			return new OfSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " of " + table;
				}
			};
		}
	}

	interface Compiled extends OfAble
	{
		default OfSelect.Compiled of(String table)
		{
			return new OfSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " of " + table;
				}
			};
		}
	}
}