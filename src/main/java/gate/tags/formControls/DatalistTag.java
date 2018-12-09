package gate.tags.formControls;

import gate.converter.Converter;
import gate.tags.DynamicAttributeTag;
import java.io.IOException;
import java.util.List;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.LambdaExpression;
import javax.el.StandardELContext;
import javax.servlet.jsp.JspException;

public class DatalistTag extends DynamicAttributeTag
{

	private List<Object> options;
	private LambdaExpression label;
	private LambdaExpression value;

	public void setOptions(List<Object> options)
	{
		this.options = options;
	}

	public void setLabel(LambdaExpression label)
	{
		this.label = label;
	}

	public void setValue(LambdaExpression value)
	{
		this.value = value;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		ELContext context = new StandardELContext(ExpressionFactory.newInstance());

		getJspContext().getOut().println(String.format("<datalist %s>",
			getAttributes().toString()));

		if (options != null)
			for (Object option : options)
			{
				Object optionLabel = option;
				if (label != null)
					optionLabel = label.invoke(context, option);

				Object optionValue = option;
				if (value != null)
					optionValue = value.invoke(context, option);

				if (getJspBody() != null)
				{
					getJspContext().getOut()
						.print(String.format("<option data-value='%s'>",
							Converter.toString(optionValue)));

					getJspContext().setAttribute("option", option);
					getJspBody().invoke(null);
					getJspContext().removeAttribute("option");

					getJspContext().getOut().print("</option>");
				} else
					getJspContext().getOut().println(String.format("<option data-value='%s'>%s</option>",
						Converter.toString(optionValue), Converter.toText(optionLabel)));

			}

		getJspContext().getOut().println("</datalist>");

	}
}
