package gate.sql.condition;

import gate.lang.property.Property;
import gate.sql.Clause;
import java.util.stream.Stream;

class RootClause implements Clause
{

	@Override
	public String toString()
	{
		return "";
	}

	@Override
	public Clause getClause()
	{
		return this;
	}

	@Override
	public Clause rollback()
	{
		return this;
	}

	@Override
	public Stream<Object> getParameters()
	{
		return Stream.empty();
	}

	@Override
	public Stream<Property> getProperties()
	{
		return Stream.empty();
	}
}
