package gate.sql.select;

import gate.sql.Clause;

public interface Lockable extends Clause
{

	/**
	 * Locks the selected rows so no other transaction can modify them until the current
	 * transaction commits or rolls back.
	 *
	 * @return the current builder, for chained invocations
	 */
	LockedSelect forUpdate();

	interface Constant extends Lockable
	{

		default LockedSelect.Constant forUpdate()
		{
			return new LockedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " for update";
				}
			};
		}
	}

	interface Generic extends Lockable
	{
		default LockedSelect.Generic forUpdate()
		{
			return new LockedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " for update";
				}
			};
		}
	}

	interface Compiled extends Lockable
	{
		default LockedSelect.Compiled forUpdate()
		{
			return new LockedSelect.Compiled(this)
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