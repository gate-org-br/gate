package gate.report;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LIST on a report.
 */
public class ReportList extends ReportElement
{

	private final List<Object> elements = new ArrayList<>();

	public ReportList()
	{
		super(new Style());
	}

	/**
	 * Adds a new row to the list.
	 *
	 * @param element the element to be added
	 * @return the same object, for chained invocations
	 */
	public ReportList add(String element)
	{
		elements.add(element);
		return this;
	}

	public ReportList add(ReportList list)
	{
		elements.add(list);
		return this;
	}

	public List<Object> getElements()
	{
		return elements;
	}

	public static ReportList of(JsonObject jsonObject)
	{
		ReportList list = new ReportList();

		if (jsonObject.get("style") instanceof JsonString style)
			list.style(Style.of(style));
		else if (jsonObject.get("style") instanceof JsonObject style)
			list.style(Style.of(style));

		if (jsonObject.get("elements") instanceof JsonArray elements)
			elements.stream().forEach(element ->
			{
				if (element instanceof JsonString string)
					list.add(string.toString());
				else if (element instanceof JsonArray array)
					list.add(of(new JsonObject()
							.set("style", jsonObject.get("style"))
							.set("elements", array)));
			});

		return list;
	}

	public static ReportList of(JsonArray jsonArray)
	{
		ReportList list = new ReportList();

		jsonArray.stream().forEach(element ->
		{
			if (element instanceof JsonString string)
				list.add(string.toString());
			else if (element instanceof JsonArray array)
				list.add(of(array));
		});

		return list;
	}
}
