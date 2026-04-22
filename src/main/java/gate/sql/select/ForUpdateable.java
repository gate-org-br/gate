package gate.sql.select;

import gate.sql.Clause;

public interface ForUpdateable extends Clause
{

	ForUpdateSelect forUpdate();

	interface Constant extends ForUpdateable
	{

		@Override
		default ForUpdateSelect.Constant forUpdate()
		{
			return new ForUpdateSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " for update";
				}
			};
		}
	}

	interface Generic extends ForUpdateable
	{

		@Override
		default ForUpdateSelect.Generic forUpdate()
		{
			return new ForUpdateSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " for update";
				}
			};
		}
	}

	interface Compiled extends ForUpdateable
	{

		@Override
		default ForUpdateSelect.Compiled forUpdate()
		{
			return new ForUpdateSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " for update";
				}
			};
		}
	}
}
