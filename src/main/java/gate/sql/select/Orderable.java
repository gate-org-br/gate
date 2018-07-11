package gate.sql.select;

import gate.sql.Clause;

public interface Orderable extends Clause
{

	OrderedSelect orderBy(String exp);

	public interface Constant extends Orderable
	{

		@Override
		default OrderedSelect.Constant orderBy(String expression)
		{
			return new OrderedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " order by " + expression;
				}
			};
		}

	}

	public interface Generic extends Orderable
	{

		@Override
		default OrderedSelect.Generic orderBy(String expression)
		{
			return new OrderedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " order by " + expression;
				}
			};
		}

	}

	public interface Compiled extends Orderable
	{

		@Override
		default OrderedSelect.Compiled orderBy(String expression)
		{
			return new OrderedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " order by " + expression;
				}
			};
		}

	}
}
