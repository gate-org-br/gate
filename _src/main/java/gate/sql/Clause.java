package gate.sql;

import gate.lang.property.Property;
import java.util.stream.Stream;

/**
 * A single clause of a SQL statement.
 */
public interface Clause
{

	/**
	 * Returns the preceeding clause.
	 *
	 * @return the preceeding clause
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
	 * Rollback the clause.
	 *
	 * @return the preceeding clause
	 */
	default Clause rollback()
	{
		return this;
	}

	/**
	 * Returns the stream of parameters of this clause.
	 *
	 * @return the stream of parameters of this clause
	 */
	default Stream<Object> getParameters()
	{
		return getClause().getParameters();
	}

	/**
	 * Returns the columns associated with this clause.
	 *
	 * @return the columns of associated of this clause
	 */
	default Stream<Property> getProperties()
	{
		return getClause().getProperties();
	}
}
