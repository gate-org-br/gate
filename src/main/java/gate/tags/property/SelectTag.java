package gate.tags.property;

import gate.converter.Converter;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.jsp.JspException;

public class SelectTag extends SelectorTag
{

	private String empty;

	@Override
	public void doTag() throws JspException, IOException
	{

		super.doTag();

		if (options == null)
			if (Enum.class.isAssignableFrom(getType()))
				options = Arrays.asList(getType().getEnumConstants());
			else if (Boolean.class.isAssignableFrom(getType()))
				options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
			else if (boolean.class.equals(getType()))
				options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
			else
				throw new IOException("No option defined for property " + getProperty());

		if (sortby != null)
			options = Toolkit
				.collection(options)
				.stream()
				.sorted((a, b) -> (Integer) sortby.invoke(EL_CONTEXT, a, b))
				.collect(Collectors.toList());

		getJspContext().getOut().println(String.format("<select %s>", getAttributes().toString()));

		if (empty == null)
			empty = "";
		if (!getName().endsWith("[]"))
			getJspContext().getOut().println("<option value=''>" + empty + "</option>");

		if (groups != null)
			for (Map.Entry<Object, List<Object>> group : Toolkit.collection(options).stream()
				.collect(Collectors.groupingBy(e -> groups.invoke(EL_CONTEXT, e), LinkedHashMap::new, Collectors.toList())).entrySet())
			{
				getJspContext().getOut().print("<optgroup label='" + Converter.toText(group.getKey()) + "'>");
				print(0, group.getValue());
				getJspContext().getOut().print("</optgroup>");
			}
		else
			print(0, options);

		getJspContext().getOut().println("</select>");
	}

	private void print(int level, Iterable<?> options) throws IOException, JspException
	{
		for (Object option : options)
		{
			Object value = option;
			if (values != null)
				value = values.invoke(EL_CONTEXT, option);

			Attributes attributes = new Attributes();
			if (Toolkit.collection(getValue()).contains(value))
				attributes.put("selected", "selected");
			attributes.put("value", getConverter().toString(getType(), value));

			getJspContext().getOut().print("<option " + attributes + ">");

			for (int i = 0; i < level; i++)
				getJspContext().getOut().print("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");

			if (getJspBody() != null)
			{
				getJspContext().setAttribute("option", option);
				getJspBody().invoke(null);
				getJspContext().removeAttribute("option");
			} else
			{
				Object label = option;
				if (labels != null)
					label = labels.invoke(EL_CONTEXT, option);
				getJspContext().getOut().print(Converter.toText(label));
			}

			getJspContext().getOut().print("</option>");

			if (children != null)
				print(level + 1, Toolkit.iterable(children.invoke(EL_CONTEXT, option)));
		}
	}

	public void setEmpty(String empty)
	{
		this.empty = empty;
	}
}
