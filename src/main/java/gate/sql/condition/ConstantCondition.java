package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * A condition without parameters.
 */
public class ConstantCondition extends Condition implements
        CompiledConditionMethods,
        GenericConditionMethods
{

    /**
     * A generic condition that is always true.
     */
    public static final ConstantCondition CONSTANT_TRUE
            = Condition.of("0").isEq("0");

    /**
     * A generic condition that is always false.
     */
    public static final ConstantCondition CONSTANT_FALSE
            = Condition.of("0").isEq("1");

    ConstantCondition(Clause clause)
    {
        super(clause);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantRelation and()
    {
        return new ConstantRelation(this)
        {
            @Override
            public String toString()
            {
                String string = getClause().toString();
                return string.isEmpty() ? string : string + " and";
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public ConstantRelation or()
    {
        return new ConstantRelation(this)
        {
            @Override
            public String toString()
            {
                String string = getClause().toString();
                return string.isEmpty() ? string : string + " or";
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public ConstantPredicate and(String expression)
    {
        return and().expression(expression);
    }

    /**
     * Adds a new expression with explicit parameters associated by an AND relation.
     *
     * @param expression the expression to be associated with the condition
     * @param parameters the parameters to be included on the condition
     * @return the current predicate, for chained invocations
     */
    @Override
    public CompiledPredicate and(String expression, Object... parameters)
    {
        return and().expression(expression, parameters);
    }

    /**
     * Adds a new generic sub query associated by an AND relation.
     *
     * @param subquery the sub query to be associated with the condition
     * @return the current predicate, for chained invocations
     */
    @Override
    public GenericPredicate and(Query subquery)
    {
        return and().subquery(subquery);
    }

    /**
     * Adds a new compiled sub query associated by an AND relation.
     *
     * @param subquery the sub query to be associated with the condition
     * @return the current predicate, for chained invocations
     */
    @Override
    public CompiledPredicate and(Query.Compiled subquery)
    {
        return and().subquery(subquery);
    }

    /**
     * Adds a new compiled sub query associated by an OR relation.
     *
     * @param subquery the sub query to be associated with the condition
     * @return the current predicate, for chained invocations
     */
    @Override
    public CompiledPredicate or(Query.Compiled subquery)
    {
        return or().subquery(subquery);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantPredicate or(String exp)
    {
        return or().expression(exp);
    }

    /**
     * Adds a new expression with explicit parameters associated by an OR relation.
     *
     * @param expression the expression to be associated with the condition
     * @param parameters the parameters to be included on the condition
     * @return the current predicate, for chained invocations
     */
    @Override
    public CompiledPredicate or(String expression, Object... parameters)
    {
        return or().expression(expression, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantCondition and(ConstantCondition condition)
    {
        return and().condition(condition);
    }

    /**
     * Adds a new compiled sub condition associated by an AND relation.
     *
     * @param condition the sub condition to be associated with the current condition
     * @return the current condition, for chained invocations
     */
    @Override
    public CompiledCondition and(CompiledCondition condition)
    {
        return and().condition(condition);
    }

    /**
     * Adds a new compiled sub condition associated by an OR relation.
     *
     * @param condition the sub condition to be associated with the current condition
     * @return the current condition, for chained invocations
     */
    @Override
    public CompiledCondition or(CompiledCondition condition)
    {
        return or().condition(condition);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantCondition or(ConstantCondition condition)
    {
        return or().condition(condition);
    }

    /**
     * Adds a new generic sub query associated by an OR relation.
     *
     * @param subquery the sub query to be associated with the condition
     * @return the current predicate, for chained invocations
     */
    @Override
    public GenericPredicate or(Query subquery)
    {
        return or().subquery(subquery);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantPredicate and(Query.Constant subquery)
    {
        return and().subquery(subquery);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantPredicate and(Query.Constant.Builder subquery)
    {
        return and().subquery(subquery);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantPredicate or(Query.Constant subquery)
    {
        return or().subquery(subquery);
    }

    /** {@inheritDoc} */
    @Override
    public ConstantPredicate or(Query.Constant.Builder subquery)
    {
        return or().subquery(subquery);
    }

    /**
     * Adds a new generic sub condition associated by an AND relation.
     *
     * @param condition the sub condition to be associated with the current condition
     * @return the current condition, for chained invocations
     */
    @Override
    public GenericCondition and(GenericCondition condition)
    {
        return and().condition(condition);
    }

    /**
     * Adds a new generic sub condition associated by an OR relation.
     *
     * @param condition the sub condition to be associated with the current condition
     * @return the current condition, for chained invocations
     */
    @Override
    public GenericCondition or(GenericCondition condition)
    {
        return or().condition(condition);
    }
}
