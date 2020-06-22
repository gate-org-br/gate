package gate.lang.json;

/**
 * Represents a token of JSON notation.
 *
 * @author davins
 */
public class JsonToken
{

	private final Type type;
	private final String string;

	public static final JsonToken COMMA = new JsonToken(JsonToken.Type.COMMA, ",");
	public static final JsonToken NULL = new JsonToken(JsonToken.Type.NULL, "null");
	public static final JsonToken TRUE = new JsonToken(JsonToken.Type.TRUE, "true");
	public static final JsonToken FALSE = new JsonToken(JsonToken.Type.FALSE, "false");
	public static final JsonToken EOF = new JsonToken(JsonToken.Type.EOF, null);
	public static final JsonToken DOUBLE_DOT = new JsonToken(JsonToken.Type.DOUBLE_DOT, ":");
	public static final JsonToken OPEN_ARRAY = new JsonToken(JsonToken.Type.OPEN_ARRAY, "[");
	public static final JsonToken CLOSE_ARRAY = new JsonToken(JsonToken.Type.CLOSE_ARRAY, "]");
	public static final JsonToken OPEN_OBJECT = new JsonToken(JsonToken.Type.OPEN_OBJECT, "{");
	public static final JsonToken CLOSE_OBJECT = new JsonToken(JsonToken.Type.CLOSE_OBJECT, "}");

	public JsonToken(Type type, String string)
	{
		this.type = type;
		this.string = string;
	}

	public Type getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return string;
	}

	public enum Type
	{
		TRUE, FALSE, STRING, NUMBER,
		OPEN_OBJECT, CLOSE_OBJECT, OPEN_ARRAY, CLOSE_ARRAY,
		COMMA, DOUBLE_DOT, EOF, NULL
	}
}
