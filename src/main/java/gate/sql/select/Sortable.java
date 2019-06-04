package gate.sql.select;

import gate.sql.Clause;

public interface Sortable extends Clause
{

	SortedSelect asc();

	SortedSelect desc();

	interface Constant extends Sortable
	{

		@Override
		default SortedSelect.Constant asc()
		{
			return new SortedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " asc";
				}
			};
		}

		@Override
		default SortedSelect.Constant desc()
		{
			return new SortedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " desc";
				}
			};
		}
	}

	interface Generic extends Sortable
	{

		@Override
		default SortedSelect.Generic asc()
		{
			return new SortedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " asc";
				}
			};
		}

		@Override
		default SortedSelect.Generic desc()
		{
			return new SortedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " desc";
				}
			};
		}
	}

	interface Compiled extends Sortable
	{

		@Override
		default SortedSelect.Compiled asc()
		{
			return new SortedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " asc";
				}
			};
		}

		@Override
		default SortedSelect.Compiled desc()
		{
			return new SortedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " desc";
				}
			};
		}
	}
}
