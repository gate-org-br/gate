package gate.report;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;

/**
 * A generic {@link gate.report.Report} element.
 */
public abstract class ReportElement extends Element
{

	public ReportElement(Style style)
	{
		super(style);
	}

	@Override
	public ReportElement style(Style style)
	{
		return (ReportElement) super.style(style);
	}

	public static ReportElement of(JsonElement element)
			throws IllegalArgumentException
	{
		if (element instanceof JsonArray array)
			if (array.stream().allMatch(e -> e instanceof JsonString))
				return ReportList.of(array);
			else
				return Grid.of(array);

		if (element instanceof JsonString string)
			return switch (string.getValue())
			{
				case "line-break" ->
					new LineBreak();
				case "page-break" ->
					new PageBreak();
				default ->
					string.getValue().startsWith("data:")
					? Image.of(string.getValue())
					: Header.of(string);
			};

		if (element instanceof JsonNumber)
			return Header.of(element);

		if (element instanceof JsonBoolean)
			return Header.of(element);

		if (element instanceof JsonObject object)
			if (object.get("type") instanceof JsonString type)
				return switch (type.toString())
				{
					case "line-break" ->
						new LineBreak();
					case "page-break" ->
						new PageBreak();
					case "grid" ->
						Grid.of(object);
					case "header" ->
						Header.of(object);
					case "paragraph" ->
						Paragraph.of(object);
					case "dictionary" ->
						Dictionary.of(object);
					case "list" ->
						ReportList.of(object);
					default ->
						null;
				};
			else
				return Dictionary.of(object);

		return null;
	}
}
