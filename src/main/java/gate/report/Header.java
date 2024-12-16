package gate.report;

import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.util.Objects;

public final class Header extends ReportElement
{

	private final Object value;

	public Header(Object value)
	{
		super(new Style());

		Objects.requireNonNull(value);

		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public Header style(Style style)
	{
		return (Header) super.style(style);
	}

	public static Header of(JsonObject jsonObject)
	{
		Header header = new Header(jsonObject.getString("text").orElse(""));
		if (jsonObject.get("style") instanceof JsonString style)
			header.style(Style.of(style));
		else if (jsonObject.get("style") instanceof JsonObject style)
			header.style(Style.of(style));
		return header;
	}

	public static Header of(JsonString jsonString)
	{
		return new Header(jsonString.getValue());
	}
}
