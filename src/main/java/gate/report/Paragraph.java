package gate.report;

import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.util.Objects;

public final class Paragraph extends ReportElement
{

	private final Object value;

	public Paragraph(Object value)
	{
		super(new Style().justify());

		Objects.requireNonNull(value);

		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public Paragraph style(Style style)
	{
		return (Paragraph) super.style(style);
	}

	public static Paragraph of(JsonObject jsonObject)
	{
		Paragraph paragraph = new Paragraph(jsonObject.getString("text").orElse(""));
		if (jsonObject.get("style") instanceof JsonString style)
			paragraph.style(Style.of(style));
		else if (jsonObject.get("style") instanceof JsonObject style)
			paragraph.style(Style.of(style));
		return paragraph;
	}

	public static Paragraph of(JsonString jsonString)
	{
		return new Paragraph(jsonString.getValue());
	}
}
