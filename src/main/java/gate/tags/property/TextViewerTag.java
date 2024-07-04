package gate.tags.property;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import jakarta.inject.Inject;
import jakarta.servlet.jsp.JspException;
import java.io.IOException;

public class TextViewerTag extends PropertyTag
{

	@Inject
	ELExpressionFactory expression;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		String value = "";
		if (getAttributes().containsKey("value"))
			value = Converter.toString(expression.create()
				.evaluate((String) getAttributes().remove("value")));
		else
			value = Converter.toString(getValue());

		getAttributes().put("value", value.replace('"', '\''));

		getJspContext().getOut().print("<g-text-viewer "
			+ getAttributes().toString() + "></g-text-viewer>");
	}
}
