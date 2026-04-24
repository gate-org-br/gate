package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class FilteredSelect implements
        SelectClause,
        Groupable,
        Orderable,
        Limitable,
        Unitable,
        Lockable
{

    private final Clause clause;

    public FilteredSelect(Clause clause)
    {
        this.clause = clause;
    }

    @Override
    public Clause getClause()
    {
        return clause;
    }

    public abstract static class Constant extends FilteredSelect implements
            Refinable.Constant,
            Groupable.Constant,
            Orderable.Constant,
            Limitable.Constant,
            Unitable.Constant,
            Lockable.Constant,
            Query.Constant.Builder
    {

        public Constant(Clause clause)
        {
            super(clause);
        }

        @Override
        public Query.Constant build()
        {
            return Query.of(toString()).constant();
        }
    }

    public abstract static class Compiled extends FilteredSelect implements
            Refinable.Compiled,
            Groupable.Compiled,
            Orderable.Compiled,
            Limitable.Compiled,
            Unitable.Compiled,
            Lockable.Compiled,
            Query.Compiled.Builder
    {

        public Compiled(Clause clause)
        {
            super(clause);
        }

        @Override
        public Query.Compiled build()
        {
            return Query.of(toString(), getParameters()
                    .collect(Collectors.toList()));
        }
    }
}
