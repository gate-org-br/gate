package gate.tags.property;

import gate.converter.Converter;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.jsp.JspException;

public abstract class CheckableTag extends SelectorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (options == null)
			if (Enum.class.isAssignableFrom(getType()))
				options = Arrays.asList(getType().getEnumConstants());
			else if (boolean.class.equals(getType()))
				options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
			else if (Boolean.class.isAssignableFrom(getType()))
				options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
			else if (getElementType() != null && Enum.class.isAssignableFrom(getElementType()))
				options = Arrays.asList(getElementType().getEnumConstants());
			else if (getElementType() != null && Boolean.class.isAssignableFrom(getElementType()))
				options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
			else
				throw new IOException("No option defined for property " + getProperty());

		if (sortby != null)
			options = Toolkit.collection(options)
				.stream()
				.sorted((a, b) -> (Integer) sortby.invoke(EL_CONTEXT, a, b))
				.collect(Collectors.toList());

		getJspContext().getOut().print("<g-select " + getAttributes() + ">");

		if (groups != null)
			for (Map.Entry<Object, List<Object>> group : Toolkit.collection(options).stream()
				.collect(Collectors.groupingBy(e -> groups.invoke(EL_CONTEXT, e), Collectors.toList())).entrySet())
				print(group.getValue(), 0);
		else
			print(options, 0);

		getJspContext().getOut().print("</g-select>");
	}

	private void print(Iterable<?> options, int depth) throws IOException, JspException
	{

		for (Object option : options)
		{
			Object value = option;
			if (values != null)
				value = values.invoke(EL_CONTEXT, option);

			Attributes attributes = new Attributes();
			attributes.put("type", getComponentType());
			attributes.put("name", getName());

			if (Toolkit.collection(getValue()).contains(value))
				attributes.put("checked", "checked");

			attributes.put("value", Converter.toString(value));

			getJspContext().getOut().print(String.format("<input %s/>", attributes.toString()));
			getJspContext().getOut().print("<label>");

			if (getJspBody() != null)
			{
				getJspContext().setAttribute("option", option);
				getJspBody().invoke(null);
				getJspContext().removeAttribute("option");
			} else if (labels != null)
				getJspContext().getOut()
					.print(Converter.toText(labels.invoke(EL_CONTEXT, option)));
			else
				getJspContext().getOut()
					.print(Converter.toText(option));

			getJspContext().getOut().print("</label>");

			if (children != null)
				print(Toolkit.iterable(children.invoke(EL_CONTEXT, option)), depth + 1);
		}
	}

	protected abstract String getComponentType();
}
