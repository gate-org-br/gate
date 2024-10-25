package gate.tags;

import gate.converter.Converter;
import gate.lang.property.Property;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

public class MapTag extends AttributeTag
{

	private Object entries;

	public void setEntries(Object value)
	{
		this.entries = value;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		JspWriter out = getJspContext().getOut();

		out.print("<dl " + getAttributes() + ">");

		if (entries instanceof Map<?, ?>)
			printMap(out);
		else if (entries != null)
			printObj(out);

		out.print("</dl>");
	}

	private void printMap(JspWriter out) throws IOException
	{
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) entries).entrySet())
		{
			Object key = entry.getKey();
			if (key != null)
			{
				Object val = entry.getValue();
				if (val != null)
				{
					out.print("<dt>" + Converter.toText(key) + "</dt>");
					out.print("<dd>" + Converter.toText(val) + "</dd>");
				}
			}
		}
	}

	private void printObj(JspWriter out) throws IOException
	{
		for (Property property : Property.getProperties(entries.getClass()))
		{
			String key = property.getDisplayName();
			if (key != null)
			{
				Object val = property.getValue(entries);
				if (val != null)
				{
					out.print("<dt>" + Converter.toText(key) + "</dt>");
					out.print("<dd>" + Converter.toText(val) + "</dd>");
				}
			}
		}
	}
}
