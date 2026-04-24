package gate.report;

import gate.lang.json.JsonBoolean;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a dictionary on a report.
 */
public class Dictionary extends ReportElement
{

	private String caption;
	private final Map<String, Object> elements = new LinkedHashMap<>();

	public Dictionary()
	{
		super(new Style());
	}

	/**
	 * Adds a new entry to the dictionary.
	 *
	 * @param property the property to be added
	 * @param value the value to be added
	 * @return the same object, for chained invocations
	 */
	public Dictionary put(String property, Object value)
	{
		elements.put(property, value);
		return this;
	}

	public Dictionary setCaption(String caption)
	{
		this.caption = caption;
		return this;
	}

	public String getCaption()
	{
		return caption;
	}

	public Map<String, Object> getElements()
	{
		return elements;
	}

	public static Dictionary of(JsonObject jsonObject)
	{
		Dictionary dictionary = new Dictionary();

		if (jsonObject.get("caption") instanceof JsonString caption)
			dictionary.setCaption(caption.getValue());

		var dataset = jsonObject;

		if (jsonObject.get("elements") instanceof JsonObject elements)
		{
			dataset = elements;
			if (jsonObject.get("style") instanceof JsonString style)
				dictionary.style(Style.of(style));
			else if (jsonObject.get("style") instanceof JsonObject style)
				dictionary.style(Style.of(style));

		}

		dataset.entrySet().stream().forEach(element ->
		{
			if (element.getValue() instanceof JsonString value)
				dictionary.put(element.getKey(), value.getValue());
			else if (element.getValue() instanceof JsonNumber value)
				dictionary.put(element.getKey(), value.getValue());
			else if (element.getValue() instanceof JsonBoolean value)
				dictionary.put(element.getKey(), value.getValue());
		});

		return dictionary;
	}
}
