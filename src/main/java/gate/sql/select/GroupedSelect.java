package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class GroupedSelect implements SelectClause, Orderable, Limitable, Lockable
{

    private final Clause clause;

    public GroupedSelect(Clause clause)
    {
        this.clause = clause;
    }

    /**
     * Adds a WITH ROLLUP modifier to the group by statement.
     *
     * @return the current builder, for chained invocations
     */
    public abstract WithRollupSelect withRollup();

    @Override
    public Clause getClause()
    {
        return clause;
    }

    public abstract static class Constant extends GroupedSelect implements
            Refinable.Constant,
            Orderable.Constant,
            Limitable.Constant,
            Lockable.Constant,
            Query.Constant.Builder
    {

        public Constant(Clause clause)
        {
            super(clause);
        }

        @Override
        public WithRollupSelect.Constant withRollup()
        {
            return new WithRollupSelect.Constant(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + " with rollup";
                }
            };
        }

        @Override
        public Query.Constant build()
        {
            return Query.of(toString()).constant();
        }
    }

    public static abstract class Compiled extends GroupedSelect implements
            Refinable.Compiled,
            Orderable.Compiled,
            Limitable.Compiled,
            Lockable.Compiled,
            Query.Compiled.Builder
    {

        public Compiled(Clause clause)
        {
            super(clause);
        }

        @Override
        public WithRollupSelect.Compiled withRollup()
        {
            return new WithRollupSelect.Compiled(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + " with rollup";
                }
            };
        }

        @Override
        public Query.Compiled build()
        {
            return Query.of(toString(), getParameters()
                    .collect(Collectors.toList()));
        }
    }

}
