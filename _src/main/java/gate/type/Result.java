package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.ResultConverter;
import gate.error.ConversionException;
import gate.handler.ResultHandler;
import gate.lang.json.JsonObject;
import java.io.Serializable;
import java.util.Objects;

@Handler(ResultHandler.class)
@Converter(ResultConverter.class)
public class Result implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final Type type;
	private final String message;

	private Result(Type type, String message)
	{

		this.type = Objects.requireNonNull(type);
		this.message = Objects.requireNonNull(message);
	}

	public static Result success(String message)
	{
		return new Result(Type.SUCCESS, message);
	}

	public static Result warning(String message)
	{
		return new Result(Type.WARNING, message);
	}

	public static Result error(String message)
	{
		return new Result(Type.ERROR, message);
	}

	@Override
	public String toString()
	{
		return new JsonObject()
			.setString("type", type.name())
			.setString("message", message)
			.toString();
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
		SUCCESS, WARNING, ERROR
	}

	public static Result of(String string) throws ConversionException
	{
		JsonObject jsonObject = JsonObject.parse(string);
		return new Result(Type.valueOf(jsonObject.getString("type").orElseThrow(()
			-> new ConversionException("Missing result type"))),
			jsonObject.getString("message").orElseThrow(()
				-> new ConversionException("Missing result message")));
	}
}
