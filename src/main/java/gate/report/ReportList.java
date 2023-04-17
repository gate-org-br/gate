package gate.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LIST on a report.
 */
public class ReportList extends ReportElement
{

	private final Type type;
	private final List<Object> elements = new ArrayList<>();

	public ReportList(Type type)
	{
		super(new Style());
		this.type = type;
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

	public Type getType()
	{
		return type;
	}

	public List<Object> getElements()
	{
		return elements;
	}

	public enum Type
	{
		NUMBER, LETTER, SYMBOL
	}
}
