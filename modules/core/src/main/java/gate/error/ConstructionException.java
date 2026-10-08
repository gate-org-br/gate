package gate.error;

import gate.lang.property.Attribute;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Signals that an object could not be constructed from a set of attributes.
 */
public class ConstructionException extends ConversionException
{
	public ConstructionException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ConstructionException(Class<?> type, Set<?> attributes)
	{
		super("Could not find construction strategy for %s with attributes %s".formatted(
				type.getName(), describe(attributes)));
	}

	public ConstructionException(Class<?> type,
	                             Executable executable,
	                             Set<?> attributes,
	                             Throwable cause)
	{
		super("Could not create %s using %s with attributes %s: %s".formatted(
				type.getName(), describe(executable), describe(attributes),
				cause.getMessage()), cause instanceof InvocationTargetException exception
				? exception.getTargetException() : cause);
	}

	public ConstructionException(Class<?> type,
	                             Class<?> builder,
	                             Set<?> attributes,
	                             Throwable cause)
	{
		super("Could not create %s using builder %s with attributes %s: %s"
						.formatted(type.getName(), builder.getName(), describe(attributes), cause.getMessage()),
				cause instanceof InvocationTargetException exception
						? exception.getTargetException() : cause);
	}

	public ConstructionException(Class<?> type,
	                             Collection<? extends Executable> constructors)
	{
		this("Ambiguous @Canonical construction strategy for %s. Candidates: %s"
				.formatted(type.getName(), describe(constructors)));
	}

	public ConstructionException(Class<?> type,
	                             Set<?> attributes,
	                             Collection<? extends Executable> constructors)
	{
		this("Ambiguous construction strategy for %s with attributes %s. Candidates: %s"
				.formatted(
						type.getName(), describe(attributes), describe(constructors)));
	}

	public ConstructionException(Class<?> type,
	                             Executable executable,
	                             Set<?> attributes,
	                             Parameter parameter)
	{
		this("Could not create %s using %s: missing value for primitive parameter %s (%s). Provided attributes: %s"
				.formatted(
						type.getName(), describe(executable), parameter.getName(),
						parameter.getType().getName(), describe(attributes)));
	}

	public ConstructionException(Class<?> type, Attribute attribute)
	{
		super("Unable to find method %s on builder class %s"
				.formatted(attribute, type.getName()));
	}

	public ConstructionException(String message)
	{
		super(message);
	}

	private static String describe(Set<?> attributes)
	{
		if (attributes.isEmpty())
			return "none";
		return attributes.stream().map(Object::toString).sorted().collect(Collectors.joining(", "));
	}

	private static String describe(Executable executable)
	{
		var parameters = Stream.of(executable.getParameters())
				.map(parameter -> parameter.getType().getSimpleName() + " " + parameter.getName())
				.collect(Collectors.joining(", "));
		if (executable instanceof Constructor<?> constructor)
			return "%s(%s)".formatted(constructor.getDeclaringClass().getName(), parameters);

		var method = (Method) executable;
		return "%s.%s(%s)".formatted(method.getDeclaringClass().getName(), method.getName(), parameters);
	}

	private static String describe(Collection<? extends Executable> constructors)
	{
		return constructors.stream()
				.map(ConstructionException::describe)
				.collect(Collectors.joining("; "));
	}
}