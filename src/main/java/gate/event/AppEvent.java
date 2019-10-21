package gate.event;

import gate.lang.json.JsonObject;
import java.util.Objects;

public class AppEvent
{

	private final String type;
	private final JsonObject detail;

	public AppEvent(String type)
	{
		this.type = Objects.requireNonNull(type);
		detail = new JsonObject();
	}

	public AppEvent(String type, JsonObject detail)
	{
		this.type = Objects.requireNonNull(type);
		this.detail = Objects.requireNonNull(detail);
	}

	public JsonObject toJsonObject()
	{
		return new JsonObject()
			.setString("type", type)
			.set("detail", detail);
	}

	@Override
	public String toString()
	{
		return toJsonObject().toString();
	}
}
