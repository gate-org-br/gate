package gate.lang.json;

public interface JsonSerializable
{
	JsonElement toJson();

	default JsonElement toJsonText()
	{
		return toJson();
	}
}