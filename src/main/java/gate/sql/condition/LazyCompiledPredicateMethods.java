package gate.sql.condition;

import gate.converter.Converter;
import gate.sql.Clause;

import java.util.Optional;
import java.util.function.Supplier;

public interface LazyCompiledPredicateMethods extends Clause
{

    /**
     * Compares the previously specified clause with the parameter supplied by the specified supplier,
     * if the parameter is not null, and evaluates to true if the clause is equals to the parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition eq(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " = ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is equals to the
     * parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition eq(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " = ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is not equals to
     * the parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition ne(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " <> ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is not equals to
     * the parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition ne(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " <> ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is less than the
     * specified parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition lt(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " < ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is less than the
     * specified parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition lt(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " < ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is less than or
     * equals the specified parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition le(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " <= ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is less than or
     * equals the specified parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition le(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " <= ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
     * the specified parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition gt(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " > ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
     * the specified parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition gt(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " > ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
     * or equals the parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition ge(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " >= ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
     * or equals the parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition ge(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " >= ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates if the clause is like the parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition lk(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " like ?",
                () -> Optional.ofNullable(supplier)
                        .map(Supplier::get)
                        .map(Converter::toString)
                        .map(parameter -> "%" + parameter + "%")
                        .orElse(null));
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates if the clause is like the parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition lk(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " like ?",
                () -> Optional.ofNullable(supplier)
                        .map(Supplier::get)
                        .map(Converter::toString)
                        .map(parameter -> "%" + parameter + "%")
                        .orElse(null));
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause matches the
     * regular expression.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition rx(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " rlike ?",
                () -> Optional.ofNullable(supplier)
                        .map(Supplier::get)
                        .map(Converter::toString)
                        .orElse(null));
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause matches the
     * regular expression.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition rx(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " rlike ?",
                () -> Optional.ofNullable(supplier)
                        .map(Supplier::get)
                        .map(Converter::toString)
                        .orElse(null));
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is between
     * the parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier)
    {
        return CompiledCondition.of(this, type, () -> " between ? and ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is between
     * the parameter.
     *
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition bw(Supplier<Object> supplier)
    {
        return CompiledCondition.of(this, () -> " between ? and ?", supplier);
    }

    /**
     * Compares the previously specified clause with the parameters supplied by the specified
     * suppliers, if both parameters are not null, and evaluates to true if the clause is between
     * the first parameter and the second one.
     *
     * @param type      type of the parameters to be compared with the specified clause
     * @param supplier1 supplier from where to get the first parameter to be compared
     * @param supplier2 supplier from where to get the second parameter to be compared
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier1, Supplier<T> supplier2)
    {
        return CompiledCondition.of(this, type, () -> " between ? and ?", supplier1, supplier2);
    }

    /**
     * Compares the previously specified clause with the parameters supplied by the specified
     * suppliers, if both parameters are not null, and evaluates to true if the clause is between
     * the first parameter and the second one.
     *
     * @param supplier1 supplier from where to get the first parameter to be compared
     * @param supplier2 supplier from where to get the second parameter to be compared
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default CompiledCondition bw(Supplier<Object> supplier1, Supplier<Object> supplier2)
    {
        return CompiledCondition.of(this, () -> " between ? and ?", supplier1, supplier2);
    }

    interface Rollback extends LazyCompiledPredicateMethods
    {

        @Override
        default <T> CompiledCondition eq(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition eq(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition ne(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition ne(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition lt(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition lt(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition le(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition le(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition gt(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition gt(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition ge(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition ge(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition lk(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition lk(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition rx(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition rx(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition bw(Supplier<Object> supplier)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier1, Supplier<T> supplier2)
        {
            return new CompiledCondition(getClause().rollback());
        }

        @Override
        default CompiledCondition bw(Supplier<Object> supplier1, Supplier<Object> supplier2)
        {
            return new CompiledCondition(getClause().rollback());
        }

    }
}