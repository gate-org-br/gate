package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class LimitedSelect implements SelectClause, Lockable
{

    private final Clause clause;

    public LimitedSelect(Clause clause)
    {
        this.clause = clause;
    }

    @Override
    public Clause getClause()
    {
        return clause;
    }

    public abstract static class Constant extends LimitedSelect implements
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

    public abstract static class Compiled extends LimitedSelect implements
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
