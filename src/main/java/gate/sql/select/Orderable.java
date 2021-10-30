package gate.sql.select;

import gate.sql.Clause;

public interface Orderable extends Clause
{

	OrderedSelect orderBy(String exp);

	SortedSelect ordernate(String exp);

	interface Constant extends Orderable
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

		@Override
		default SortedSelect.Constant ordernate(String expression)
		{
			return new SortedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					if (expression == null || expression.isBlank())
						return getClause().toString();
					else if (expression.charAt(0) == '+')
						return getClause() + " order by " + expression.substring(1);
					else if (expression.charAt(0) == '-')
						return getClause() + " order by " + expression.substring(1) + " desc";
					else
						return getClause() + " order by " + expression;
				}
			};
		}
	}

	interface Generic extends Orderable
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

		@Override
		default SortedSelect.Generic ordernate(String expression)
		{
			return new SortedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					if (expression == null || expression.isBlank())
						return getClause().toString();
					else if (expression.charAt(0) == '+')
						return getClause() + " order by " + expression.substring(1);
					else if (expression.charAt(0) == '-')
						return getClause() + " order by " + expression.substring(1) + " desc";
					else
						return getClause() + " order by " + expression;
				}
			};
		}

	}

	interface Compiled extends Orderable
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

		@Override
		default SortedSelect.Compiled ordernate(String expression)
		{
			return new SortedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					if (expression == null || expression.isBlank())
						return getClause().toString();
					else if (expression.charAt(0) == '+')
						return getClause() + " order by " + expression.substring(1);
					else if (expression.charAt(0) == '-')
						return getClause() + " order by " + expression.substring(1) + " desc";
					else
						return getClause() + " order by " + expression;
				}
			};
		}

	}
}
