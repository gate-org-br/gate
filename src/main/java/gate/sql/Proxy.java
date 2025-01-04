package gate.sql;

import gate.lang.property.Entity;
import gate.lang.property.Property;
import java.util.function.BiConsumer;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class Proxy
{

	public static <T> T create(Class<T> type, BiConsumer<String, Object> setter)
	{

		try
		{
			return new ByteBuddy()
					.subclass(type)
					.method(nameStartsWith("set")
							.and(isDeclaredBy(type))
							.and(takesArguments(1))
							.and(returns(void.class).or(returns(type))))
					.intercept(InvocationHandlerAdapter.of((target, method, args) ->
					{
						Object value = args[0];
						var clazz = method.getParameters()[0].getType();
						String columnName = method.getName().substring(3);

						if (Entity.isEntity(clazz))
						{
							String id = Entity.getId(clazz);
							columnName += "$" + id;
							if (value != null)
								value = Property.getProperty(clazz, id).getValue(value);
						} else
							columnName = Character.toLowerCase(columnName.charAt(0))
									+ columnName.substring(1);

						setter.accept(columnName, value);
						return method.getReturnType() == void.class ? null : target;
					}))
					.make()
					.load(Proxy.class.getClassLoader())
					.getLoaded()
					.getDeclaredConstructor()
					.newInstance();
		} catch (ReflectiveOperationException ex)
		{
			throw new InstantiationError("Error trying to create proxy class");
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(T object, BiConsumer<String, Object> setter)
	{

		try
		{
			return (T) new ByteBuddy()
					.subclass(object.getClass())
					.method(nameStartsWith("set")
							.and(isDeclaredBy(object.getClass()))
							.and(takesArguments(1))
							.and(returns(void.class).or(returns(object.getClass()))))
					.intercept(InvocationHandlerAdapter.of((target, method, args) ->
					{
						Object value = args[0];
						method.invoke(object, value);
						var clazz = method.getParameters()[0].getType();
						String columnName = method.getName().substring(3);

						if (Entity.isEntity(clazz))
						{
							String id = Entity.getId(clazz);
							columnName += "$" + id;
							if (value != null)
								value = Property.getProperty(clazz, id).getValue(value);
						} else
							columnName = Character.toLowerCase(columnName.charAt(0))
									+ columnName.substring(1);

						setter.accept(columnName, value);
						return method.getReturnType() == void.class ? null : target;
					}))
					.make()
					.load(Proxy.class.getClassLoader())
					.getLoaded()
					.getDeclaredConstructor()
					.newInstance();
		} catch (ReflectiveOperationException ex)
		{
			throw new InstantiationError("Error trying to create proxy class");
		}
	}
}
