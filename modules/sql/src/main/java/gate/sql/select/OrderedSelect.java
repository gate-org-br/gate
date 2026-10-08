package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class OrderedSelect implements SelectClause, Limitable, Sortable, Lockable
{

    private final Clause clause;

    public OrderedSelect(Clause clause)
    {
        this.clause = clause;
    }

    @Override
    public Clause getClause()
    {
        return clause;
    }

    public abstract OrderedSelect and(String exp);

    public abstract static class Constant extends OrderedSelect implements
            Orderable.Constant,
            Sortable.Constant,
            Limitable.Constant,
            Lockable.Constant,
            Query.Constant.Builder
    {

        public Constant(Clause clause)
        {
            super(clause);
        }

        @Override
        public OrderedSelect.Constant and(String exp)
        {
            return new OrderedSelect.Constant(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + ", " + exp;
                }
            };
        }

        @Override
        public Query.Constant build()
        {
            return Query.of(toString()).constant();
        }
    }

    public abstract static class Compiled extends OrderedSelect implements
            Orderable.Compiled,
            Sortable.Compiled,
            Limitable.Compiled,
            Lockable.Constant,
            Query.Compiled.Builder
    {

        public Compiled(Clause clause)
        {
            super(clause);
        }

        @Override
        public OrderedSelect.Compiled and(String exp)
        {
            return new OrderedSelect.Compiled(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + ", " + exp;
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
