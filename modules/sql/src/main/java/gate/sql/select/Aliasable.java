package gate.sql.select;

import gate.sql.Clause;

/**
 * A clause that can have a alias
 */
public interface Aliasable
{

	/**
	 * Defines an alias to the previously specified expression.
	 *
	 * @param alias the alias to be associated with the previously specified expression
	 *
	 * @return the current builder, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Clause as(String alias);
}
