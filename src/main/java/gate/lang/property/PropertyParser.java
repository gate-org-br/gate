package gate.lang.property;

import gate.converter.Converter;
import gate.error.ConversionException;
import gate.error.PropertyError;
import gate.util.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

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
		return property(new PropertyScanner(property));
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

		if (token instanceof String)
			return javaIdentifier(scanner);

		if (token.equals('['))
			return collection(scanner);

		throw new PropertyError("Invalid property name: %s.", property);
	}

	private Attribute dottedAttribute(PropertyScanner scanner)
	{
		if (!token.equals('.'))
			return attribute(scanner);

		token = scanner.next();

		if ("this".equals(token))
			return self(scanner);

		if (token instanceof String)
			return attribute(scanner);

		throw new PropertyError("Invalid property name: %s.", property);
	}

	private Attribute javaIdentifier(PropertyScanner scanner)
	{
		if (!(token instanceof String name))
			throw new PropertyError("Invalid property name: %s.", property);

		token = scanner.next();
		Attribute attribute = attributes.get(attributes.size() - 1);

		if (Objects.equals(token, '('))
		{
			List<Object> parameters = parameters(scanner);

			try
			{
				Method method = attribute.getRawType().getMethod(name,
						parameters.stream().map(Object::getClass).toArray(Class[]::new));

				if (method.getReturnType() == void.class)
					throw new PropertyError("Method %s has no return type.", method.toString());

				return new MethodAttribute(method, parameters.toArray());
			} catch (NoSuchMethodException e)
			{
				return null;
			}
		}

		// Map access
		if (Map.class.isAssignableFrom(attribute.getRawType()))
		{
			if (attribute.getGenericType() instanceof ParameterizedType parameterizedType)
			{
				Class<?> keyType = Reflection.getRawType(
						parameterizedType.getActualTypeArguments()[0]);

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
		}

		// Field access
		for (Class<?> superclass = attribute.getRawType(); superclass != null;
		     superclass = superclass.getSuperclass())
		{
			Field field = Arrays.stream(superclass.getDeclaredFields())
					.filter(e -> e.getName().equals(name))
					.findAny()
					.orElse(null);

			if (field != null)
			{
				Class<?> type = field.getType();
				if (type == boolean.class)
					return new BooleanFieldAttribute(field);
				if (type == char.class)
					return new CharFieldAttribute(field);
				if (type == byte.class)
					return new ByteFieldAttribute(field);
				if (type == short.class)
					return new ShortFieldAttribute(field);
				if (type == int.class)
					return new IntFieldAttribute(field);
				if (type == long.class)
					return new LongFieldAttribute(field);
				if (type == float.class)
					return new FloatFieldAttribute(field);
				if (type == double.class)
					return new DoubleFieldAttribute(field);
				return FieldAttribute.of(field);
			}
		}

		return null;
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
		if (token instanceof Boolean || token instanceof Number || token instanceof String)
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
			// Array access
			if (clazz.isArray())
			{
				if (name instanceof Number number)
					return new ArrayAttribute(clazz.getComponentType(), number.intValue());

				return null;
			}

			// List access
			if (List.class.isAssignableFrom(clazz))
			{
				if (name instanceof Number number)
					return new ListAttribute(attribute.getElementType(), number.intValue());

				return null;
			}

			// Map access
			if (Map.class.isAssignableFrom(clazz))
			{
				if (name instanceof Number || name instanceof Boolean || name instanceof String)
				{
					if (name instanceof String
					    && attribute.getGenericType() instanceof ParameterizedType paramType)
					{
						Class<?> keyType = Reflection.getRawType(
								paramType.getActualTypeArguments()[0]);

						try
						{
							name = Converter.getConverter(keyType).ofString(keyType, (String) name);
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
		}

		// Collection access (no index)
		if (Collection.class.isAssignableFrom(clazz))
			return new CollectionAttribute(attribute.getElementType());

		return null;
	}
}