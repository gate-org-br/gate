package gate.sql.select;

import gate.sql.Clause;
import java.util.List;
import java.util.Objects;

public interface Groupable extends Clause
{

	/**
	 * Defines the group by clause of the query.
	 *
	 * @param columns the columns of the group by clause
	 *
	 * @return the current builder, for chained invocations
	 */
	GroupedSelect groupBy(List<String> columns);

	/**
	 * Defines the group by clause of the query.
	 *
	 * @param columns the columns of the group by clause
	 *
	 * @return the current builder, for chained invocations
	 */
	GroupedSelect groupBy(String... columns);

	interface Constant extends Groupable
	{

		@Override
		default GroupedSelect.Constant groupBy(List<String> columns)
		{
			Objects.requireNonNull(columns, "Attempt to define a null group by clause");
			return new GroupedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return columns.isEmpty() ? getClause().toString() : getClause() + " group by " + String.join(", ", columns);
				}
			};
		}

		@Override
		default GroupedSelect.Constant groupBy(String... columns)
		{
			Objects.requireNonNull(columns, "Attempt to define a null group by clause");
			return new GroupedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return columns.length == 0 ? getClause().toString() : getClause() + " group by " + String.join(", ", columns);
				}
			};
		}
	}

	interface Generic extends Groupable
	{

		@Override
		default GroupedSelect.Generic groupBy(List<String> columns)
		{
			Objects.requireNonNull(columns, "Attempt to define a null group by clause");
			return new GroupedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return columns.isEmpty() ? getClause().toString() : getClause() + " group by " + String.join(", ", columns);
				}
			};
		}

		@Override
		default GroupedSelect.Generic groupBy(String... columns)
		{
			Objects.requireNonNull(columns, "Attempt to define a null group by clause");
			return new GroupedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return columns.length == 0 ? getClause().toString() : getClause() + " group by " + String.join(", ", columns);
				}
			};
		}

	}

	interface Compiled extends Groupable
	{

		@Override
		default GroupedSelect.Compiled groupBy(List<String> columns)
		{
			Objects.requireNonNull(columns, "Attempt to define a null group by clause");
			return new GroupedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return columns.isEmpty() ? getClause().toString() : getClause() + " group by " + String.join(", ", columns);
				}
			};
		}

		@Override
		default GroupedSelect.Compiled groupBy(String... columns)
		{
			Objects.requireNonNull(columns, "Attempt to define a null group by clause");
			return new GroupedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return columns.length == 0 ? getClause().toString() : getClause() + " group by " + String.join(", ", columns);
				}
			};
		}

	}
}
