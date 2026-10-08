package gate.sql.condition;

import gate.sql.Clause;

import java.util.function.Supplier;

/**
 * A single predicate of a compiled condition.
 */
public class LazyCompiledPredicate extends Predicate
        implements ConstantPredicateMethods,
        LazyCompiledPredicateMethods
{

    LazyCompiledPredicate(Clause clause)
    {
        super(clause);
    }


    public CompiledCondition isNull()
    {
        return apply(() -> this + " is null");
    }

    public CompiledCondition isNotNull()
    {
        return apply(() -> this + " is not null");
    }

    public CompiledCondition isEq(String expression)
    {
        return apply(() -> this + " = " + expression);
    }

    public CompiledCondition isNe(String expression)
    {
        return apply(() -> this + " <> " + expression);
    }

    public CompiledCondition isLt(String expression)
    {
        return apply(() -> this + " < " + expression);
    }

    public CompiledCondition isLe(String expression)
    {
        return apply(() -> this + " <= " + expression);
    }

    public CompiledCondition isGt(String expression)
    {
        return apply(() -> this + " > " + expression);
    }

    public CompiledCondition isGe(String expression)
    {
        return apply(() -> this + " >= " + expression);
    }

    public CompiledCondition isBw(String expression1, String expression2)
    {
        return apply(() -> this + " between " + expression1 + " and " + expression2);
    }

    private CompiledCondition apply(Supplier<String> expression)
    {
        return new CompiledCondition(getClause())
        {
            @Override
            public String toString()
            {
                return expression.get();
            }
        };
    }

    static class Rollback extends LazyCompiledPredicate
            implements LazyCompiledPredicateMethods.Rollback
    {
        public Rollback(Clause clause)
        {
            super(clause.rollback());
        }

        @Override
        public CompiledCondition isNull()
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isNotNull()
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isEq(String expression)
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isNe(String expression)
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isLt(String expression)
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isLe(String expression)
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isGt(String expression)
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isGe(String expression)
        {
            return new CompiledCondition(getClause());
        }

        @Override
        public CompiledCondition isBw(String expression1, String expression2)
        {
            return new CompiledCondition(getClause());
        }
    }
}
