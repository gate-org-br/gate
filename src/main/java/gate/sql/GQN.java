package gate.sql;

import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.PropertyCondition;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interpreter of the Gate Query Notation.
 *
 * 
 *
 * @author Davi Nunes da Silva
 */
public class GQN<T>
{

	private final Class<T> type;
	private final String[] notation;

	/**
	 * Creates a new GQN from a java type and notation.
	 *
	 * @param type java type associated with the condition
	 * @param notation GQN notation to be used to create the condition
	 */
	public GQN(Class<T> type, String... notation)
	{
		this.type = type;
		this.notation = notation;
	}

	public List<Property> getProperties()
	{
		return Stream.of(notation)
			.map(GQN::getProperty)
			.map(e -> Property.getProperty(type, e))
			.collect(Collectors.toList());
	}

	/**
	 * Creates a new condition from a java type and GQN notation.
	 *
	 * @param getName function used to define the column name to be associated with the property
	 *
	 * @return The new condition created or null if no test prefix is found
	 *
	 * @throws IllegalArgumentException if the GQN notation specified is invalid
	 * @throws gate.error.NoSuchPropertyError if a tested property is invalid
	 */
	public PropertyCondition getCondition(Function<Property, String> getName)
	{
		PropertyCondition condition = PropertyCondition.PROPERTY_TRUE;

		for (String string : notation)
		{
			Property property = Property
				.getProperty(type, GQN.getProperty(string));
			switch (GQN.getCondition(string))
			{
				case "=":
					condition = condition.and(getName.apply(property)).isEq(property);
					break;
				case ">":
					condition = condition.and(getName.apply(property)).isGt(property);
					break;
				case "<":
					condition = condition.and(getName.apply(property)).isLt(property);
					break;
				case "%":
					condition = condition.and(getName.apply(property)).isLk(property);
					break;
				case "@":
					condition = condition.and(getName.apply(property)).isNull();
					break;
				case "!=":
					condition = condition.and(getName.apply(property)).isNe(property);
					break;
				case "!>":
					condition = condition.and(getName.apply(property)).isLe(property);
					break;
				case "!<":
					condition = condition.and(getName.apply(property)).isGe(property);
					break;
				case "!%":
					condition = condition.and().not(getName.apply(property)).isLk(property);
					break;
				case "!@":
					condition = condition.and().not(getName.apply(property)).isNull();
					break;
				default:
					break;
			}
		}

		return condition;
	}

	/**
	 * Creates a new condition for a java object and GQN notation ignoring null properties.
	 *
	 * @param object object to be used as condition parameter
	 *
	 * @return The new condition created or null if no test prefix is found
	 *
	 * @throws IllegalArgumentException if the GQN notation specified is invalid
	 * @throws gate.error.NoSuchPropertyError if a tested property is invalid
	 */
	public CompiledCondition getCondition(T object)
	{
		CompiledCondition condition = CompiledCondition.COMPILED_TRUE;
		for (String string : notation)
		{
			Property property = Property.getProperty(type, gate.sql.GQN.getProperty(string));
			String predicate = gate.sql.GQN.getCondition(string);
			switch (predicate)
			{
				case "@":
					condition = condition.and(Entity.getFullColumnName(property)).isNull();
					break;
				case "!@":
					condition = condition.and().not(Entity.getFullColumnName(property)).isNull();
					break;
				default:
					Object value = property.getValue(object);
					if (value != null)
						switch (predicate)
						{
							case "=":
								condition = condition.and(Entity.getFullColumnName(property)).eq(value);
								break;
							case "!=":
								condition = condition.and(Entity.getFullColumnName(property)).ne(value);
								break;
							case ">":
								condition = condition.and(Entity.getFullColumnName(property)).gt(value);
								break;
							case "!<":
								condition = condition.and(Entity.getFullColumnName(property)).ge(value);
								break;
							case "<":
								condition = condition.and(Entity.getFullColumnName(property)).lt(value);
								break;
							case "!>":
								condition = condition.and(Entity.getFullColumnName(property)).le(value);
								break;
							case "%":
								condition = condition.and(Entity.getFullColumnName(property)).lk(value);
								break;
							case "!%":
								condition = condition.and().not(Entity.getFullColumnName(property)).lk(value);
								break;
						}
			}
		}

		return condition;
	}

	public OrderBy.Ordering getOrderBy()
	{
		OrderBy.Ordering ordering = null;
		for (String string : notation)
		{
			if (ordering == null)
				switch (GQN.getOrdering(string))
				{
					case "+":
						ordering = OrderBy.asc(GQN.getProperty(string));
						break;
					case "-":
						ordering = OrderBy.desc(GQN.getProperty(string));
						break;
				}
			else
				switch (GQN.getOrdering(string))
				{
					case "+":
						ordering = ordering.asc(GQN.getProperty(string));
						break;
					case "-":
						ordering = ordering.desc(GQN.getProperty(string));
						break;
				}
		}
		return ordering;
	}

	private static String getCondition(String GQN)
	{
		String condition = "";
		for (int i = 0; i < GQN.length(); i++)
			switch (GQN.charAt(i))
			{
				case '!':
					switch (condition)
					{
						case "":
							condition = "!";
							break;
						case "=":
							condition = "!=";
							break;
						case ">":
							condition = "!>";
							break;
						case "<":
							condition = "!<";
							break;
						case "%":
							condition = "!%";
							break;
						case "@":
							condition = "!@";
							break;
						default:
							throw new IllegalArgumentException(GQN);
					}
					break;
				case '=':
					switch (condition)
					{
						case "":
							condition = "=";
							break;
						case "!":
							condition = "!=";
							break;
						case ">":
							condition = ">=";
							break;
						case "<":
							condition = "<=";
							break;
						default:
							throw new IllegalArgumentException(GQN);
					}
					break;
				case '>':
					switch (condition)
					{
						case "":
							condition = ">";
							break;
						case "!":
							condition = "!>";
							break;
						case "=":
							condition = "!<";
							break;
						case "<":
							condition = "!=";
							break;
						default:
							throw new IllegalArgumentException(GQN);
					}
					break;
				case '<':
					switch (condition)
					{
						case "":
							condition = "<";
							break;
						case "!":
							condition = "!<";
							break;
						case "=":
							condition = "!>";
							break;
						case ">":
							condition = "!=";
							break;
						default:
							throw new IllegalArgumentException(GQN);
					}
					break;
				case '%':
					switch (condition)
					{
						case "":
							condition = "%";
							break;
						case "!":
							condition = "!%";
							break;
						default:
							throw new IllegalArgumentException(GQN);
					}
					break;
				case '@':
					switch (condition)
					{
						case "":
							condition = "@";
							break;
						case "!":
							condition = "!@";
							break;
						default:
							throw new IllegalArgumentException(GQN);
					}
					break;

				case '+':
				case '-':
					break;

				default:
					return condition;
			}

		return condition;
	}

	private static String getOrdering(String GQN)
	{
		String ordering = "";
		for (int i = 0; i < GQN.length(); i++)
			switch (GQN.charAt(i))
			{
				case '+':
					if (!ordering.equals(""))
						throw new IllegalArgumentException(GQN);
					ordering = "+";
					break;
				case '-':
					if (!ordering.equals(""))
						throw new IllegalArgumentException(GQN);
					ordering = "-";
					break;
				case '!':
				case '=':
				case '>':
				case '<':
				case '%':
				case '@':
					break;
				default:
					return ordering;
			}
		return ordering;
	}

	private static String getProperty(String GQN)
	{
		int i = 0;
		while (i < GQN.length()
			&& (GQN.charAt(i) == '!'
			|| GQN.charAt(i) == '='
			|| GQN.charAt(i) == '>'
			|| GQN.charAt(i) == '<'
			|| GQN.charAt(i) == '%'
			|| GQN.charAt(i) == '@'
			|| GQN.charAt(i) == '+'
			|| GQN.charAt(i) == '-'))
			i++;

		if (i == GQN.length())
			throw new IllegalArgumentException(GQN);

		return GQN.substring(i);
	}
}
