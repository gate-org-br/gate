package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.ResultConverter;
import gate.error.ConversionException;
import gate.handler.ResultHandler;
import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.io.Serializable;
import java.util.Objects;

@Handler(ResultHandler.class)
@Converter(ResultConverter.class)
public class Result implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final Type type;
	private JsonElement data;
	private final String message;

	private Result(Type type, String message, JsonElement data)
	{

		this.type = Objects.requireNonNull(type);
		this.message = Objects.requireNonNull(message);
		this.data = Objects.requireNonNull(data);
	}

	public static Result success(String message)
	{
		return new Result(Type.SUCCESS, message, JsonNull.INSTANCE);
	}

	public static Result warning(String message)
	{
		return new Result(Type.WARNING, message, JsonNull.INSTANCE);
	}

	public static Result error(String message)
	{
		return new Result(Type.ERROR, message, JsonNull.INSTANCE);
	}

	public static Result success(String message, JsonElement data)
	{
		return new Result(Type.SUCCESS, message, data);
	}

	public static Result warning(String message, JsonElement data)
	{
		return new Result(Type.WARNING, message, data);
	}

	public static Result error(String message, JsonElement data)
	{
		return new Result(Type.ERROR, message, data);
	}

	public static Result success(String message, String data)
	{
		return new Result(Type.SUCCESS, message, JsonString.of(data));
	}

	public static Result warning(String message, String data)
	{
		return new Result(Type.WARNING, message, JsonString.of(data));
	}

	public static Result error(String message, String data)
	{
		return new Result(Type.ERROR, message, JsonString.of(data));
	}

	public static Result success(String message, long data)
	{
		return new Result(Type.SUCCESS, message, JsonNumber.of(data));
	}

	public static Result warning(String message, long data)
	{
		return new Result(Type.WARNING, message, JsonNumber.of(data));
	}

	public static Result error(String message, long data)
	{
		return new Result(Type.ERROR, message, JsonNumber.of(data));
	}

	public static Result success(String message, double data)
	{
		return new Result(Type.SUCCESS, message, JsonNumber.of(data));
	}

	public static Result warning(String message, double data)
	{
		return new Result(Type.WARNING, message, JsonNumber.of(data));
	}

	public static Result error(String message, double data)
	{
		return new Result(Type.ERROR, message, JsonNumber.of(data));
	}

	public static Result success(String message, boolean data)
	{
		return new Result(Type.SUCCESS, message, JsonBoolean.of(data));
	}

	public static Result warning(String message, boolean data)
	{
		return new Result(Type.WARNING, message, JsonBoolean.of(data));
	}

	public static Result error(String message, boolean data)
	{
		return new Result(Type.ERROR, message, JsonBoolean.of(data));
	}

	public static Result success(String message, Object data)
	{
		return new Result(Type.SUCCESS, message, JsonElement.of(data));
	}

	public static Result warning(String message, Object data)
	{
		return new Result(Type.WARNING, message, JsonElement.of(data));
	}

	public static Result error(String message, Object data)
	{
		return new Result(Type.ERROR, message, JsonElement.of(data));
	}

	@Override
	public String toString()
	{
		return new JsonObject()
			.setString("type", type.name())
			.setString("message", message)
			.set("data", data)
			.toString();
	}

	public JsonElement getData()
	{
		return data;
	}

	public void setData(JsonElement data)
	{
		this.data = data;
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
		return new Result(Type.valueOf(jsonObject.getString("type").orElseThrow(() -> new ConversionException("Missing result type"))),
			jsonObject.getString("message").orElseThrow(() -> new ConversionException("Missing result message")),
			jsonObject.getJsonElement("data").orElseThrow(() -> new ConversionException("Missing result data")));
	}
}
