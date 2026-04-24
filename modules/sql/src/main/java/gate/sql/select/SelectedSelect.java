package gate.sql.select;

import gate.sql.Clause;
import gate.sql.condition.ConstantCondition;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class SelectedSelect implements SelectClause, Groupable, Orderable, Limitable, Refinable, Lockable
{

    private final Clause clause;

    public SelectedSelect(Clause clause)
    {
        this.clause = clause;
    }

    @Override
    public Clause getClause()
    {
        return clause;
    }

    public abstract static class Constant extends SelectedSelect implements
            Joinable.Constant,
            Filterable.Constant,
            Refinable.Constant,
            Groupable.Constant,
            Orderable.Constant,
            Limitable.Constant,
            Unitable.Constant,
            Aliasable,
            Lockable.Constant,
            Query.Constant.Builder
    {

        public Constant(Clause clause)
        {
            super(clause);
        }

        @Override
        public RefinedSelect.Constant having(ConstantCondition predicate)
        {
            return new RefinedSelect.Constant(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + " having " + predicate.toString();
                }
            };
        }

        @Override
        public Aliased.Constant as(String alias)
        {
            return new Aliased.Constant(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + " as " + alias;
                }
            };
        }

        @Override
        public Query.Constant build()
        {
            return Query.of(toString()).constant();
        }
    }

    public abstract static class Compiled extends SelectedSelect implements
            Joinable.Compiled,
            Filterable.Compiled,
            Refinable.Compiled,
            Groupable.Compiled,
            Orderable.Compiled,
            Limitable.Compiled,
            Unitable.Compiled,
            Lockable.Compiled,
            Aliasable,
            Query.Compiled.Builder
    {

        public Compiled(Clause clause)
        {
            super(clause);
        }

        @Override
        public Aliased.Compiled as(String alias)
        {
            return new Aliased.Compiled(this)
            {
                @Override
                public String toString()
                {
                    return getClause() + " as " + alias;
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

    public static abstract class Aliased extends SelectedSelect
    {

        public Aliased(Clause clause)
        {
            super(clause);
        }

        public abstract static class Constant extends Aliased implements
                Joinable.Constant,
                Filterable.Constant,
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

        public abstract static class Compiled extends Aliased implements
                Joinable.Compiled,
                Filterable.Compiled,
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

}
