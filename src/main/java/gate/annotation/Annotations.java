package gate.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public class Annotations
{

	public static boolean exists(Class<? extends Annotation> annotation,
		Class<?> type, Method method)
	{
		return method.isAnnotationPresent(annotation)
			|| type.isAnnotationPresent(annotation)
			|| type.getPackage().isAnnotationPresent(annotation);
	}

	public static boolean exists(Class<? extends Annotation> annotation,
		String className,
		String methodName) throws ClassNotFoundException, NoSuchMethodException
	{
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		return exists(annotation, clazz, clazz.getMethod(methodName));
	}

	public static boolean exists(Class<? extends Annotation> annotation,
		String module,
		String screen,
		String action) throws ClassNotFoundException, NoSuchMethodException
	{
		return exists(annotation, screen != null ? module + "." + screen + "Screen" : module + ".Screen", action != null ? "call" + action : "call");
	}

	public static Optional<Annotation> search(Class<? extends Annotation> annotation,
		Class<?> clazz, Method method)
	{
		if (method.isAnnotationPresent(annotation))
			return Optional.of(method.getAnnotation(annotation));
		if (clazz.isAnnotationPresent(annotation))
			return Optional.of(clazz.getAnnotation(annotation));
		if (clazz.getPackage().isAnnotationPresent(annotation))
			return Optional.of(clazz.getPackage().getAnnotation(annotation));
		return Optional.empty();
	}

	public static Optional<Annotation> search(Class<? extends Annotation> annotation,
		String className,
		String methodName) throws ClassNotFoundException, NoSuchMethodException
	{
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		return search(annotation, clazz, clazz.getMethod(methodName));
	}

	public static Optional<Annotation> search(Class<? extends Annotation> annotation,
		String module,
		String screen,
		String action) throws ClassNotFoundException, NoSuchMethodException
	{
		return search(annotation, screen != null
			? module + "." + screen
			+ "Screen" : module + ".Screen", action != null ? "call" + action : "call");
	}
}
