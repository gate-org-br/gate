package gate.sql.delete;

import gate.sql.SQLBuilder;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.GenericCondition;
import java.util.Collections;
import gate.sql.statement.Sentence;
import java.util.stream.Collectors;

/**
 * Delete statement bound to a table name.
 *
 * @author Davi Nunes da Silva
 */
public class TableDelete implements Delete, Sentence.Builder
{

	private final String table;

	TableDelete(String name)
	{
		table = name;
	}

	/**
	 * Specifies a condition to the delete sentence builder.
	 *
	 * @param condition condition to be associated with the builder
	 *
	 * @return the same builder with the condition specified
	 */
	public Sentence.Builder where(GenericCondition condition)
	{
		return () -> Sentence.of("delete from " + table + " where " + condition);
	}

	/**
	 * Binds a condition to the delete statement
	 *
	 * @param condition to be bound to the update statement
	 *
	 * @return A SQLBuilder with the conditions specified
	 */
	public Sentence.Compiled.Builder where(ConstantCondition condition)
	{
		return () -> Sentence.of("delete from " + table + " where " + condition)
				.parameters(Collections.singletonList(Collections.emptyList()));
	}

	/**
	 * Binds a condition to the delete statement
	 *
	 * @param condition to be bound to the update statement
	 *
	 * @return A SQLBuilder with the conditions specified
	 */
	public Sentence.Compiled.Builder where(CompiledCondition condition)
	{
		return () -> Sentence.of("delete from " + table + " where " + condition).parameters(condition.getParameters().collect(Collectors.toList()));
	}

	/**
	 * Creates a full table delete sentence
	 *
	 * @return a full table delete sentence
	 * @see SQLBuilder
	 * @author Davi Nunes da Silva
	 */
	@Override
	public Sentence build()
	{
		return Sentence.of("delete from " + table);
	}
}
