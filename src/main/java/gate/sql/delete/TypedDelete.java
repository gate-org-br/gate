package gate.sql.delete;

import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.PropertyCondition;
import gate.sql.statement.Operation;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Delete statement bound to a entity java class
 *
 * 
 *
 * @author Davi Nunes da Silva
 */
public class TypedDelete<T> implements Delete, Operation.Builder<T>
{

	private final Class<T> type;

	TypedDelete(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Creates a SQL delete sentence based on the id.
	 *
	 * @return a SQL delete sentence based on the id
	 */
	@Override
	public Operation<T> build()
	{
		Property property = Property.getProperty(type, Entity.getId(type));

		return where(Condition.of(property.getColumnName()).isEq(property)).build();
	}

	/**
	 * Binds a condition to the delete statement
	 *
	 * @param condition to be bound to the delete statement
	 *
	 * @return A SQLBuilder with the conditions specified
	 */
	public Operation.Builder<T> where(ConstantCondition condition)
	{
		return () -> Operation.of(type, Collections.emptyList(), TypedDelete.this + " where " + condition);
	}

	/**
	 * Binds a condition to the delete statement
	 *
	 * @param condition to be bound to the delete statement
	 *
	 * @return A SQLBuilder with the conditions specified
	 */
	public Operation.Builder<T> where(PropertyCondition condition)
	{
		return () -> Operation.of(type,
			condition.getProperties().collect(Collectors.toList()),
			TypedDelete.this + " where " + condition);
	}

	@Override
	public String toString()
	{
		return "delete from " + Entity.getFullTableName(type);
	}
}
