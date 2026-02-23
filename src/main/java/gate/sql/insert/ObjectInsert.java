package gate.sql.insert;

import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.Objects;

/**
 * Insert builder bound to a source object.
 * <p>
 * Column values are extracted from the source object using property references.
 */
public class ObjectInsert<T> implements Insert
{

    private final T object;
    private final ClassInsert<T> insert;

    ObjectInsert(Class<T> type, T object)
    {
        this.object = Objects.requireNonNull(object);
        this.insert = new ClassInsert<>(type);
    }

    /**
     * Adds an ignore modifier to the sentence.
     *
     * @return the same builder with ignore enabled
     */
    public ObjectInsert<T> ignore()
    {
        insert.ignore();
        return this;
    }

    /**
     * Adds a new column/value pair, extracting value from the bound object.
     *
     * @param property property reference used to resolve column and value
     * @param <R>      property type
     * @return compiled builder with the added column/value pair
     */
    public <R> Compiled set(PropertyReference<T, R> property)
    {
        return compiled().set(property);
    }

    /**
     * Adds new column/value pairs, extracting values from the bound object.
     *
     * @param properties property references used to resolve columns and values
     * @return compiled builder with the added column/value pairs
     */
    @SafeVarargs
    public final Compiled set(PropertyReference<T, ?>... properties)
    {
        return compiled().set(properties);
    }

    /**
     * Creates a compiled builder bound to this source object.
     *
     * @return compiled object insert builder
     */
    public Compiled compiled()
    {
        return new Compiled();
    }

    /**
     * Adds the next column(s) only if previous specified condition is true.
     *
     * @param assertion condition to be checked
     * @return conditional builder
     */
    public When when(boolean assertion)
    {
        return assertion ? new When() : new DisabledWhen();
    }

    /**
     * SQL insert sentence builder bound to a source object with values extracted from property references.
     */
    public class Compiled implements Sentence.Compiled.Builder
    {

        private final ClassInsert<T>.Compiled delegate;

        private Compiled()
        {
            this.delegate = insert.new Compiled();
        }

        /**
         * Adds a new column/value pair, extracting value from the bound object.
         *
         * @param property property reference used to resolve column and value
         * @param <R>      property type
         * @return the same builder with the added column/value pair
         */
        public <R> Compiled set(PropertyReference<T, R> property)
        {
            PropertyReference<T, R> reference = Objects.requireNonNull(property);
            delegate.set(reference, reference.apply(object));
            return this;
        }

        /**
         * Adds new column/value pairs, extracting values from the bound object.
         *
         * @param properties property references used to resolve columns and values
         * @return the same builder with the added column/value pairs
         */
        @SafeVarargs
        public final Compiled set(PropertyReference<T, ?>... properties)
        {
            for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
                setUnchecked(Objects.requireNonNull(property));
            return this;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private void setUnchecked(PropertyReference<T, ?> property)
        {
            PropertyReference<T, Object> reference = (PropertyReference) property;
            delegate.set(reference, reference.apply(object));
        }

        /**
         * Adds the next column(s) only if previous specified condition is true.
         *
         * @param assertion condition to be checked
         * @return conditional builder
         */
        public When when(boolean assertion)
        {
            return assertion ? new When() : new DisabledWhen();
        }

        @Override
        public Sentence.Compiled build()
        {
            return delegate.build();
        }

        @Override
        public String toString()
        {
            return delegate.toString();
        }

        /**
         * Conditional helper for compiled object insert builders.
         */
        public class When
        {
            /**
             * Adds a new column/value pair if previous specified condition was true.
             *
             * @param property property reference used to resolve column and value
             * @param <R>      property type
             * @return the same builder with the added column/value pair
             */
            public <R> Compiled set(PropertyReference<T, R> property)
            {
                return Compiled.this.set(property);
            }

            /**
             * Adds new column/value pairs if previous specified condition was true.
             *
             * @param properties property references used to resolve columns and values
             * @return the same builder with the added column/value pairs
             */
            @SuppressWarnings({"varargs", "unchecked"})
            public Compiled set(PropertyReference<T, ?>... properties)
            {
                return Compiled.this.set(properties);
            }

            /**
             * Adds the next column(s) only if previous specified condition is true.
             *
             * @param assertion condition to be checked
             * @return conditional builder
             */
            public When when(boolean assertion)
            {
                return assertion ? this : new DisabledWhen();
            }
        }

        /**
         * Conditional helper for ignored branches on compiled object insert builders.
         */
        public class DisabledWhen extends When
        {
            /**
             * Ignores the specified column/value pair and keeps current builder unchanged.
             *
             * @param property property reference used to resolve column and value
             * @param <R>      property type
             * @return the current compiled builder unchanged
             */
            @Override
            public <R> Compiled set(PropertyReference<T, R> property)
            {
                return Compiled.this;
            }

            /**
             * Ignores the specified column/value pairs and keeps current builder unchanged.
             *
             * @param properties property references used to resolve columns and values
             * @return the current compiled builder unchanged
             */
            @Override
            @SuppressWarnings({"varargs", "unchecked"})
            public Compiled set(PropertyReference<T, ?>... properties)
            {
                return Compiled.this;
            }

            /**
             * Keeps this conditional helper disabled for subsequent operations.
             *
             * @param assertion condition to be checked
             * @return this disabled conditional helper
             */
            @Override
            public When when(boolean assertion)
            {
                return this;
            }
        }
    }

    /**
     * Conditional helper for object insert builders.
     */
    public class When
    {
        /**
         * Adds a new column/value pair if previous specified condition was true.
         *
         * @param property property reference used to resolve column and value
         * @param <R>      property type
         * @return compiled builder with the added column/value pair
         */
        public <R> Compiled set(PropertyReference<T, R> property)
        {
            return compiled().set(property);
        }

        /**
         * Adds new column/value pairs if previous specified condition was true.
         *
         * @param properties property references used to resolve columns and values
         * @return compiled builder with the added column/value pairs
         */
        @SuppressWarnings({"varargs", "unchecked"})
        public Compiled set(PropertyReference<T, ?>... properties)
        {
            return compiled().set(properties);
        }

        /**
         * Adds the next column(s) only if previous specified condition is true.
         *
         * @param assertion condition to be checked
         * @return conditional builder
         */
        public When when(boolean assertion)
        {
            return assertion ? this : new DisabledWhen();
        }
    }

    /**
     * Conditional helper for ignored branches on object insert builders.
     */
    public class DisabledWhen extends When
    {
        /**
         * Ignores the specified column/value pair and returns an empty compiled builder.
         *
         * @param property property reference used to resolve column and value
         * @param <R>      property type
         * @return an empty compiled builder
         */
        @Override
        public <R> Compiled set(PropertyReference<T, R> property)
        {
            return compiled();
        }

        /**
         * Ignores the specified column/value pairs and returns an empty compiled builder.
         *
         * @param properties property references used to resolve columns and values
         * @return an empty compiled builder
         */
        @Override
        @SuppressWarnings({"varargs", "unchecked"})
        public Compiled set(PropertyReference<T, ?>... properties)
        {
            return compiled();
        }

        /**
         * Keeps this conditional helper disabled for subsequent operations.
         *
         * @param assertion condition to be checked
         * @return this disabled conditional helper
         */
        @Override
        public When when(boolean assertion)
        {
            return this;
        }
    }
}
