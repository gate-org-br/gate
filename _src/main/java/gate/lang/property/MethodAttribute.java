package gate.lang.property;

import gate.error.PropertyError;
import gate.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class MethodAttribute implements JavaIdentifierAttribute
{

	private final Method method;
	private final Object[] parameters;

	MethodAttribute(Method method, Object[] parameters)
	{
		this.method = method;
		this.parameters = parameters;
	}

	@Override
	public Type getGenericType()
	{
		return method.getGenericReturnType();
	}

	@Override
	public Type getElementType()
	{
		if (method.getReturnType().isAnnotationPresent(ElementType.class))
			return method.getReturnType().getAnnotation(ElementType.class).value();
		else if (method.getReturnType().isArray())
			return method.getReturnType().getComponentType();
		else if (List.class.isAssignableFrom(method.getReturnType())
				&& method.getGenericReturnType() instanceof ParameterizedType)
			return ((ParameterizedType) method.getGenericReturnType())
					.getActualTypeArguments()[0];
		else if (Map.class.isAssignableFrom(method.getReturnType())
				&& method.getGenericReturnType() instanceof ParameterizedType)
			return ((ParameterizedType) method.getGenericReturnType())
					.getActualTypeArguments()[1];
		return Object.class;
	}

	@Override
	public Class<?> getRawType()
	{
		return method.getReturnType();
	}

	@Override
	public Object getValue(Object object)
	{
		try
		{
			if (object == null)
				return null;
			method.setAccessible(true);
			return method.invoke(object, parameters);
		} catch (IllegalAccessException
				| InvocationTargetException e)
		{
			throw new PropertyError("Error trying to call method %s",
					method.getName());
		}
	}

	@Override
	public Object forceValue(Object object)
	{
		return getValue(object);
	}

	@Override
	public void setValue(Object object, Object value)
	{
		throw new PropertyError("Attempt to set value of a method.");
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MethodAttribute
				&& Objects.equals(method, ((MethodAttribute) obj).method)
				&& Arrays.equals(parameters, ((MethodAttribute) obj).parameters);
	}

	@Override
	public int hashCode()
	{
		return method.hashCode();
	}

	@Override
	public String toString()
	{
		return method.toString();
	}
}
