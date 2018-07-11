package gate.type;

import java.io.Serializable;
import java.util.Objects;

public class Status<T extends Serializable> implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final T value;
	private final Type type;
	private final String message;

	private Status(Type type, String message, T value)
	{

		this.type = Objects.requireNonNull(type);
		this.message = Objects.requireNonNull(message);
		this.value = value;
	}

	public static <T extends Serializable> Status<T> error(String message)
	{
		return new Status(Type.ERROR, message, null);
	}

	public static <T extends Serializable> Status<T> success(String message, T value)
	{
		return new Status(Type.SUCCESS, message, value);
	}

	public static <T extends Serializable> Status<T> success(String message)
	{
		return new Status(Type.SUCCESS, message, null);
	}

	public T getValue()
	{
		return value;
	}

	public Type getType()
	{
		return type;
	}

	public String getMessage()
	{
		return message;
	}

	public enum Type
	{
		SUCCESS, ERROR
	}
}
