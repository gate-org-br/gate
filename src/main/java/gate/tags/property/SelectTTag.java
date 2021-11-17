package gate.tags.property;

import gate.converter.Converter;
import gate.lang.property.Property;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.jsp.JspException;

public class SelectTTag extends PropertyTag
{

	private String empty;
	private Object options;
	private String children;
	private String identifiedBy;

	public void setEmpty(String empty)
	{
		this.empty = empty;
	}

	public void setChildren(String children)
	{
		this.children = children;
	}

	public void setOptions(Object options)
	{
		this.options = options;
	}

	public void setIdentifiedBy(String identifiedBy)
	{
		this.identifiedBy = identifiedBy;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		List<Object> o = new ArrayList<>(Toolkit.list(this.options));
		getJspContext().getOut().println(String.format("<select %s>", getAttributes().toString()));
		getJspContext().getOut().println(String.format("<option value=''>%s</option>",
			this.empty == null ? "" : empty));
		for (Object child : o)
			write(child, 0);
		getJspContext().getOut().println("</select>");
	}

	public void write(Object obj, int depth) throws JspException, IOException
	{
		Attributes attributes = new Attributes();

		Object value = identifiedBy != null ? Property.getValue(obj, identifiedBy) : obj;
		attributes.put("value", Converter.toString(value));
		if (Toolkit.collection(getValue()).contains(value))
			attributes.put("selected", "selected");

		if (getJspBody() != null)
		{
			getJspContext().getOut().print(String.format("<option %s style='text-indent: %dpx'>", attributes.toString(),
				depth * 20));
			getJspContext().setAttribute("option", obj);
			getJspContext().getOut().print(Stream.generate(() -> "&nbsp;&rarr;&nbsp;").limit(depth).collect(Collectors.joining()));
			getJspBody().invoke(getJspContext().getOut());
			getJspContext().removeAttribute("option");
			getJspContext().getOut().println("</option>");
		} else
			getJspContext().getOut().println(String.format("<option %s>%s%s</option>", attributes.toString(),
				Stream.generate(() -> "--------").limit(depth).collect(Collectors.joining()), Converter
				.toText(obj)));

		for (Object child : gate.util.Toolkit.collection(Property.getProperty(obj.getClass(), children).getValue(obj)))
			write(child, depth + 1);
	}
}
