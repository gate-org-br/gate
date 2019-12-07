package gate.tags;

import gate.annotation.Color;
import gate.annotation.Icon;
import gate.error.AppError;
import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class IconTag extends DynamicAttributeTag
{

	private Object type;

	public void setType(Object type)
	{
		this.type = type;
	}

	@Override
	public void doTag()
	{
		try
		{
			super.doTag();

			if (!getAttributes().containsKey("style"))
				Color.Extractor.extract(type)
					.ifPresent(e -> getAttributes().put("style", "color: " + e));

			Icons.Icon icon = Icon.Extractor.extract(type).orElse(Icons.UNKNOWN);
			if (icon.getCode().length() == 1)
				getJspContext().getOut().print(String.format("<i c %s>%s</i>",
					getAttributes().toString(), icon.getCode()));
			else
				getJspContext().getOut().print(String.format("<i %s>&#x%s;</i>", getAttributes(), icon.getCode()));
		} catch (JspException | IOException e)
		{
			throw new AppError(e);
		}
	}
}
