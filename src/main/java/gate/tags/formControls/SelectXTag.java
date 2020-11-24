package gate.tags.formControls;

import gate.converter.Converter;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.LambdaExpression;
import javax.el.StandardELContext;
import javax.servlet.jsp.JspException;

public class SelectXTag extends PropertyTag
{

	private Iterable<?> options;

	private LambdaExpression values;
	private LambdaExpression labels;
	private LambdaExpression sortby;

	protected final ELContext EL_CONTEXT
		= new StandardELContext(ExpressionFactory.newInstance());

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (options == null)
			if (Enum.class.isAssignableFrom(getType()))
				options = Arrays.asList(getType().getEnumConstants());
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

		getJspContext().getOut().print("<g-select " + getAttributes() + " >");

		for (Object option : options)
		{
			Object value = option;
			if (values != null)
				value = values.invoke(EL_CONTEXT, option);

			Attributes attributes = new Attributes();
			attributes.putAll(getAttributes());
			attributes.put("type", "checkbox");
			attributes.put("name", getName());

			if (Toolkit.collection(getValue()).contains(value))
				attributes.put("checked", "checked");

			attributes.put("value", Converter.toString(value));

			getJspContext().getOut().print("<label>");
			getJspContext().getOut().print(String.format("<input %s/>", attributes.toString()));

			if (getJspBody() != null)
			{
				getJspContext().setAttribute("option", option);
				getJspBody().invoke(null);
				getJspContext().removeAttribute("option");
			} else if (labels != null)
				getJspContext().getOut().print(Converter.toText(labels.invoke(EL_CONTEXT, option)));
			else
				getJspContext().getOut().print(Converter.toText(option));

			getJspContext().getOut().print("</label>");
		}

		getJspContext().getOut().print("</g-select>");
	}

	public void setOptions(Object options)
	{
		this.options = Toolkit.iterable(options);
	}

	public void setLabels(LambdaExpression labels)
	{
		this.labels = labels;
	}

	public void setValues(LambdaExpression values)
	{
		this.values = values;
	}

	public void setSortby(LambdaExpression sortby)
	{
		this.sortby = sortby;
	}

}
