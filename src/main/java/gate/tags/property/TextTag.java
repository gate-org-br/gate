package gate.tags.property;

import gate.type.Attributes;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.LambdaExpression;
import javax.el.StandardELContext;
import javax.servlet.jsp.JspException;

public class TextTag extends PropertyTag
{

	private Object options;
	private LambdaExpression labels;
	private LambdaExpression values;

	private static final AtomicInteger SEQUENCE = new AtomicInteger();

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (options != null)
		{
			Attributes attributes = new Attributes();
			String datalist = "datalist-" + SEQUENCE.incrementAndGet();
			attributes.put("id", datalist);
			getAttributes().put("list", datalist);

			ELContext context = new StandardELContext(ExpressionFactory.newInstance());

			if (options instanceof String)
			{
				attributes.put("data-options", options);
				getJspContext().getOut().println("<datalist " + attributes + "></datalist>");
			} else
			{
				getJspContext().getOut().println("<datalist " + attributes + ">");
				for (Object option : Toolkit.iterable(options))
				{
					Object optionLabel = option;
					if (labels != null)
						optionLabel = labels.invoke(context, option);

					Object optionValue = option;
					if (values != null)
						optionValue = values.invoke(context, option);

					if (getJspBody() != null)
					{
						getJspContext().getOut()
							.print("<option data-value='" + getConverter().toString(getType(), optionValue) + "'>");

						getJspContext().setAttribute("option", option);
						getJspBody().invoke(null);
						getJspContext().removeAttribute("option");

						getJspContext().getOut().print("</option>");
					} else
						getJspContext().getOut().println(String.format("<option data-value='%s'>%s</option>",
							getConverter().toString(getType(), optionValue), getConverter().toText(getType(), optionLabel)));

				}
				getJspContext().getOut().println("</datalist>");
			}
		}

		if (!getAttributes().containsKey("type"))
			getAttributes().put("type", "text");
		if (!getAttributes().containsKey("value"))
			getAttributes().put("value", getName().endsWith("[]") ? "" : getConverter().toString(getType(), getValue()));
		getJspContext().getOut().print("<input " + getAttributes().toString() + " />");

	}

	public void setOptions(Object options)
	{
		this.options = options;
	}

	public void setLabels(LambdaExpression labels)
	{
		this.labels = labels;
	}

	public void setValues(LambdaExpression values)
	{
		this.values = values;
	}

}
