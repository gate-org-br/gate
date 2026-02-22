package gate.sql.condition;

import gate.converter.Converter;
import gate.sql.Clause;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface LazyCompiledPredicateMethods
{

    /**
     * Compares the previously specified clause with the parameter supplied by the specified
     * supplier, if the parameter is not null, and evaluates to true if the clause is equals to the
     * parameter.
     *
     * @param type     type of the parameter to be compared with the specified clause
     * @param supplier supplier from where to get the parameter to be compared with the specified
     *                 clause
     * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
     * @throws java.lang.NullPointerException if any of the parameters is null
     */
    default <T> CompiledCondition eq(Class<T> type, Supplier<T> supplier)
    {
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " = ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " = ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " <> ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " <> ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " < ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " < ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " <= ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " <= ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " > ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " > ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " >= ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " >= ?";
            }
        };
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

        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(),
                        Stream.of("%" + Converter.toString(parameter) + "%"));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " like ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(),
                        Stream.of("%" + Converter.toString(parameter) + "%"));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " like ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(),
                        Stream.of(Converter.toString(parameter)));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " rlike ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(Converter.toString(parameter)));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " rlike ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = Objects.requireNonNull(supplier.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " between ? and ?";
            }
        };
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
        Objects.requireNonNull(supplier, "Attempt to compile a null supplier into a condition");
        var parameter = supplier.get();
        if (parameter == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " between ? and ?";
            }
        };
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
        Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
        Objects.requireNonNull(supplier1, "Attempt to compile a null supplier into a condition");
        Objects.requireNonNull(supplier2, "Attempt to compile a null supplier into a condition");
        var parameter1 = Objects.requireNonNull(supplier1.get(), "Attempt to compile a null parameter into a condition");
        var parameter2 = Objects.requireNonNull(supplier2.get(), "Attempt to compile a null parameter into a condition");
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(), Stream.of(parameter1, parameter2));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " between ? and ?";
            }
        };
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
        Objects.requireNonNull(supplier1, "Attempt to compile a null supplier into a condition");
        Objects.requireNonNull(supplier2, "Attempt to compile a null supplier into a condition");
        var parameter1 = supplier1.get();
        var parameter2 = supplier2.get();
        if (parameter1 == null || parameter2 == null)
            return new CompiledCondition(getClause().rollback());
        return new CompiledCondition(getClause())
        {
            @Override
            public Stream<Object> getParameters()
            {
                return Stream.concat(getClause().getParameters(),
                        Stream.of(parameter1, parameter2));
            }

            @Override
            public String toString()
            {
                return LazyCompiledPredicateMethods.this + " between ? and ?";
            }
        };
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

    Clause getClause();
}
