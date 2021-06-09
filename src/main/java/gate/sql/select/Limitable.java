package gate.sql.select;

import gate.sql.Clause;

public interface Limitable extends Clause
{

	/**
	 * Limits the number of rows to be returned by the query.
	 *
	 * @param value the maximum number of rows to be returned
	 *
	 * @return the current builder, for chained invocations
	 */
	LimitedSelect limit(int value);

	/**
	 * Limits the number of rows to be returned by the query.
	 *
	 * @param offset the first row to be included
	 * @param count the maximum number or rows to be included
	 *
	 * @return the current builder, for chained invocations
	 */
	LimitedSelect limit(int offset, int count);

	/**
	 * Limits the number of rows to be returned by the query.
	 *
	 * @param pageSize the size of the page of rows to be returned
	 * @param pageIndex the index of the page to be returned
	 *
	 * @return the current builder, for chained invocations
	 */
	LimitedSelect paginate(int pageSize, int pageIndex);

	interface Constant extends Limitable
	{

		@Override
		default LimitedSelect.Constant limit(int value)
		{
			return new LimitedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + value;
				}
			};
		}

		@Override
		default LimitedSelect.Constant limit(int offset, int count)
		{
			return new LimitedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + offset + ", " + count;
				}
			};
		}

		@Override
		default LimitedSelect.Constant paginate(int pageSize, int pageIndex)
		{
			return new LimitedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + (pageIndex * pageSize) + ", " + pageSize;
				}
			};
		}
	}

	interface Generic extends Limitable
	{

		@Override
		default LimitedSelect.Generic limit(int value)
		{
			return new LimitedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + value;
				}
			};
		}

		@Override
		default LimitedSelect.Generic limit(int offset, int count)
		{
			return new LimitedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + offset + ", " + count;
				}
			};
		}

		@Override
		default LimitedSelect.Generic paginate(int pageSize, int pageIndex)
		{
			return new LimitedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + (pageIndex * pageSize) + ", " + pageSize;
				}
			};
		}

	}

	interface Compiled extends Limitable
	{

		@Override
		default LimitedSelect.Compiled limit(int value)
		{
			return new LimitedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + value;
				}
			};
		}

		@Override
		default LimitedSelect.Compiled limit(int offset, int count)
		{
			return new LimitedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + offset + ", " + count;
				}
			};
		}

		@Override
		default LimitedSelect.Compiled paginate(int pageSize, int pageIndex)
		{
			return new LimitedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " limit " + (pageIndex * pageSize) + ", " + pageSize;
				}
			};
		}
	}
}
