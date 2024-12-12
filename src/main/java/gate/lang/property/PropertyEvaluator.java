package gate.lang.property;

import gate.error.PropertyError;
import gate.util.Reflection;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class PropertyEvaluator
{

	private Object token;
	private final String property;

	public PropertyEvaluator(String property)
	{
		this.property = property;
	}

	public Object evaluate(Object object)
	{
		try (PropertyScanner scanner = new PropertyScanner(property))
		{
			token = scanner.next();

			if (token != null && object != null)
			{
				object = attribute(scanner, object);
				while (token != null && object != null)
					object = dottedAttribute(scanner, object);
			}

			return object;
		} catch (ReflectiveOperationException | IOException ex)
		{
			throw new PropertyError("Invalid property name: %s.", property);
		}
	}

	private Object attribute(PropertyScanner scanner, Object object) throws ReflectiveOperationException
	{
		if ("this".equals(token))
			return self(scanner, object);
		else if (token instanceof String)
			return javaIdentifier(scanner, object);
		else if (token.equals('['))
			return collection(scanner, object);
		throw new PropertyError("Invalid property name: %s.", property);
	}

	private Object self(PropertyScanner scanner, Object object)
	{
		if (!"this".equals(token))
			throw new PropertyError("Invalid property name: %s.", property);
		token = scanner.next();
		return object;
	}

	private Object dottedAttribute(PropertyScanner scanner, Object object) throws ReflectiveOperationException
	{
		if (token.equals('.'))
		{
			token = scanner.next();
			if ("this".equals(token))
				return self(scanner, object);
			else if (token instanceof String)
				return attribute(scanner, object);
			else
				throw new PropertyError("Invalid property name: %s.", property);
		}
		return attribute(scanner, object);
	}

	private Object javaIdentifier(PropertyScanner scanner, Object object) throws ReflectiveOperationException
	{
		if (!(token instanceof String))
			throw new PropertyError("Invalid property name: %s.", property);

		String name = (String) token;
		token = scanner.next();

		if (Objects.equals(token, '('))
		{
			List<Object> parameters = parameters(scanner);

			Method method = Reflection
					.findMethod(object.getClass(),
							name,
							parameters.stream()
									.map(Object::getClass)
									.toArray(Class[]::new))
					.orElse(null);

			if (method == null)
				method = Reflection.findMethod(object.getClass(), name,
						parameters.stream().map(Object::getClass)
								.map(e -> switch (e.getSimpleName())
						{
							case "Integer" ->
								int.class;
							case "Boolean" ->
								boolean.class;
							case "Character" ->
								char.class;
							default ->
								e;
						}).toArray(Class[]::new))
						.orElse(null);

			if (method != null)
				return method.invoke(object, parameters.toArray());

			return null;

		} else
		{
			if (object instanceof Map<?, ?> map)
				return map.get(name);

			Method getter = Reflection.findMethod(object.getClass(), "get"
					+ Character.toUpperCase(name.charAt(0))
					+ name.substring(1)).orElse(null);
			if (getter != null)
				return getter.invoke(object);

			Field field = Reflection.findField(object.getClass(), name)
					.orElse(null);

			if (field != null)
				return field.get(object);

			return null;
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

	private Object collection(PropertyScanner scanner, Object object)
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

		if (name != null)
		{
			if (object instanceof Object[] array
					&& name instanceof Number number)
				return array[number.intValue()];
			else if (object instanceof List<?> list
					&& name instanceof Number number)
				return list.get(number.intValue());
			else if (object instanceof Map<?, ?> map)
				return map.get(name);
		}

		return null;
	}
}
