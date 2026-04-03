package gate.sql;

import gate.lang.property.Property;
import java.util.stream.Stream;

/**
 * Single clause of a SQL statement.
 */
public interface Clause
{

	/**
	 * Returns the preceding clause.
	 *
	 * @return the preceding clause
	 */
	Clause getClause();

	/**
	 * Returns the clause SQL string.
	 *
	 * @return the clause SQL string
	 */
	@Override
	String toString();

	/**
	 * Rolls the clause back to its previous valid state.
	 *
	 * @return the preceding valid clause
	 */
	default Clause rollback()
	{
		return this;
	}

	/**
	 * Returns the parameters contributed by this clause.
	 *
	 * @return stream of parameters contributed by this clause
	 */
	default Stream<Object> getParameters()
	{
		return getClause().getParameters();
	}

	/**
	 * Returns the properties referenced by this clause.
	 *
	 * @return stream of properties referenced by this clause
	 */
	default Stream<Property> getProperties()
	{
		return getClause().getProperties();
	}
}
