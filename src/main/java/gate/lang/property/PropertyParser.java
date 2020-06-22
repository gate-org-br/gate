package gate.lang.property;

import gate.error.PropertyError;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.util.Reflection;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class PropertyParser
{

	private Object token;
	private final Class<?> type;
	private final String property;
	private final List<Attribute> attributes = new ArrayList<>();

	public PropertyParser(Class<?> type, String property)
	{
		this.type = type;
		this.property = property;
	}

	public Property parse()
	{
		try (PropertyScanner scanner = new PropertyScanner(property))
		{
			return property(scanner);
		} catch (IOException e)
		{
			throw new PropertyError("Error on trying to parse property: %s", e.getMessage());
		}
	}

	private Property property(PropertyScanner scanner)
	{
		token = scanner.next();

		if (!token.equals("this"))
			attributes.add(new SelfAttribute(type));

		Attribute attribute = attribute(scanner);
		if (attribute == null)
			return null;
		attributes.add(attribute);

		while (token != null)
		{
			Attribute dottedAttribute = dottedAttribute(scanner);
			if (dottedAttribute == null)
				return null;
			attributes.add(dottedAttribute);
		}
		return new Property(type, property, attributes);
	}

	private Attribute self(PropertyScanner scanner)
	{
		if (!"this".equals(token))
			throw new PropertyError("Invalid property name: %s.", property);
		token = scanner.next();
		return new SelfAttribute(type);
	}

	private Attribute attribute(PropertyScanner scanner)
	{
		if ("this".equals(token))
			return self(scanner);
		else if (token instanceof String)
			return javaIdentifier(scanner);
		else if (token.equals('['))
			return collection(scanner);
		throw new PropertyError("Invalid property name: %s.", property);
	}

	private Attribute dottedAttribute(PropertyScanner scanner)
	{
		if (token.equals('.'))
		{
			token = scanner.next();
			if ("this".equals(token))
				return self(scanner);
			else if (token instanceof String)
				return attribute(scanner);
			else
				throw new PropertyError("Invalid property name: %s.", property);
		}
		return attribute(scanner);
	}

	private Attribute javaIdentifier(PropertyScanner scanner)
	{
		if (!(token instanceof String))
			throw new PropertyError("Invalid property name: %s.", property);

		String name = (String) token;
		token = scanner.next();

		Attribute attribute = attributes.get(attributes.size() - 1);

		if (Objects.equals(token, '('))
		{
			List<Object> parameters = parameters(scanner);

			try
			{
				Method method = attribute.getRawType().getMethod(name,
					parameters.stream().map(Object::getClass).toArray(Class[]::new));
				if (method.getReturnType() == null)
					throw new PropertyError("Method %s has no return type.", method.toString());
				return new MethodAttribute(method, parameters.toArray());
			} catch (NoSuchMethodException e)
			{
				return null;
			}
		} else
		{
			if (Map.class.isAssignableFrom(attribute.getRawType()))
			{
				if (attribute.getGenericType() instanceof ParameterizedType)
				{
					Class keyType = Reflection.getRawType(((ParameterizedType) attribute.getGenericType())
						.getActualTypeArguments()[0]);
					try
					{
						return new MapAttribute(attribute.getElementType(),
							Converter.getConverter(keyType).ofString(keyType, name));
					} catch (ConversionException ex)
					{
						throw new PropertyError("Error on trying to parse property: %s",
							ex.getMessage());
					}
				}

				return new MapAttribute(attribute.getElementType(), name);
			} else
			{
				for (Class<?> superclass = attribute.getRawType();
					superclass != null; superclass = superclass.getSuperclass())
				{
					Field field = Arrays.stream(superclass.getDeclaredFields()).filter(e -> e.getName().equals(name))
						.findAny().orElse(null);
					if (field != null)
						return new FieldAttribute(field);
				}
				return null;
			}
		}
	}

	private List<Object> parameters(PropertyScanner scanner)
	{
		if (!Objects.equals(token, '('))
			throw new PropertyError("Invalid property name: %s.", property);
		token = scanner.next();

		List<Object> parameters = new ArrayList<>();
		if (!Objects.equals(')', token))
		{
			parameters.add(parameter(scanner));
			while (Objects.equals(token, ','))
			{
				token = scanner.next();
				parameters.add(parameter(scanner));
			}
		}

		if (!Objects.equals(token, ')'))
			throw new PropertyError("Invalid property name: %s.", property);
		token = scanner.next();

		return parameters;
	}

	private Object parameter(PropertyScanner scanner)
	{
		if (token instanceof Boolean
			|| token instanceof Number
			|| token instanceof String)
		{
			Object result = token;
			token = scanner.next();
			return result;
		}

		throw new PropertyError("Invalid property name: %s.", property);
	}

	private Attribute collection(PropertyScanner scanner)
	{
		if (!token.equals('['))
			throw new PropertyError("Invalid property name: %s.", property);
		token = scanner.next();

		Object name = null;

		if (!token.equals(']'))
		{
			name = token;
			token = scanner.next();

			if (!token.equals(']'))
				throw new PropertyError("Invalid property name: %s.", property);
		}

		token = scanner.next();

		Attribute attribute = attributes.get(attributes.size() - 1);
		Class<?> clazz = attribute.getRawType();

		if (name != null)
		{
			if (clazz.isArray())
			{
				if (name instanceof Long
					|| name instanceof Integer
					|| name instanceof Short
					|| name instanceof Byte)
					return new ArrayAttribute(clazz.getComponentType(), ((Number) name).intValue());
				return null;
			} else if (List.class.isAssignableFrom(clazz))
			{
				if (name instanceof Long
					|| name instanceof Integer
					|| name instanceof Short
					|| name instanceof Byte)
					return new ListAttribute(attribute.getElementType(), ((Number) name).intValue());
				return null;

			} else if (Map.class.isAssignableFrom(clazz))
			{
				if (name instanceof Long
					|| name instanceof Integer
					|| name instanceof Short
					|| name instanceof Byte
					|| name instanceof Double
					|| name instanceof Float
					|| name instanceof Boolean
					|| name instanceof String)
				{
					if (name instanceof String
						&& attribute.getGenericType() instanceof ParameterizedType)
					{
						Class keyType = Reflection.getRawType(((ParameterizedType) attribute.getGenericType())
							.getActualTypeArguments()[0]);
						try
						{
							name = Converter.getConverter(keyType)
								.ofString(keyType, (String) name);
						} catch (ConversionException ex)
						{
							throw new PropertyError("Error on trying to parse property: %s",
								ex.getMessage());
						}
					}

					return new MapAttribute(attribute.getElementType(), name);
				}

				return null;
			}

			return null;
		} else if (Collection.class.isAssignableFrom(clazz))
		{
			return new CollectionAttribute(attribute.getElementType());
		} else
		{
			return null;
		}
	}
}
