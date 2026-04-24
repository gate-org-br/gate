package gate.sql.delete;

import gate.annotation.Entity;
import gate.lang.property.Property;
import gate.sql.ColumnReference;
import gate.sql.annotation.Table;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.ExtractorCondition;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Delete statement bound to a entity java class
 *
 * @author Davi Nunes da Silva
 */
public class TypedDelete<T> implements Delete, Sentence.Extractor.Compiled.Builder<T>
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
	public Sentence.Extractor.Compiled<T> build()
	{
		Property property = Property.getProperty(type, Entity.Extractor.extract(type));
		return where(Condition.from(type).expression(Entity.Extractor.extract(type))
				.eq(property::getValue)).build();
	}

	/**
	 * Binds a condition to the delete statement
	 *
	 * @param condition to be bound to the delete statement
	 * @return A SQLBuilder with the conditions specified
	 */
	public Sentence.Extractor.Compiled.Builder<T> where(ConstantCondition condition)
	{
		return () -> Sentence.of(TypedDelete.this + " where " + condition)
				.from(type).parameters(List.of());
	}

	/**
	 * Binds a condition to the delete statement
	 *
	 * @param condition to be bound to the delete statement
	 * @return A SQLBuilder with the conditions specified
	 */
	@SuppressWarnings("unchecked")
	public Sentence.Extractor.Compiled.Builder<T> where(ExtractorCondition<T> condition)
	{
		List<Function<T, ?>> parameters = new ArrayList<>();
		condition.getParameters().forEach(e -> parameters.add((Function<T, ?>) e));
		return () -> Sentence.of(TypedDelete.this + " where " + condition)
				.from(type).parameters(parameters);
	}

	public <R> Sentence.Compiled.Builder where(PropertyReference<T, R> property, R value)
	{
		CompiledCondition condition = Condition.of(ColumnReference.of(property).name()).eq(value);
		return () -> Sentence.of(TypedDelete.this + " where " + condition)
				.parameters(condition.getParameters().toList());
	}

	public <R> Sentence.Extractor.Compiled.Builder<T> where(PropertyReference<T, R> property,
	                                                        Function<T, R> extractor)
	{
		return where(Condition.from(type).expression(property).eq(extractor::apply));
	}

	@Override
	public String toString()
	{
		return "delete from " + Table.Extractor.getFullName(type);
	}
}