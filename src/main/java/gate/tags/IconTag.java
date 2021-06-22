package gate.tags;

import gate.annotation.Color;
import gate.annotation.Icon;
import gate.error.AppError;
import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class IconTag extends AttributeTag
{

	private Object type;
	private Object empty;

	public void setType(Object type)
	{
		this.type = type;
	}

	public void setEmpty(Object empty)
	{
		this.empty = empty;
	}

	@Override
	public void doTag()
	{
		try
		{
			super.doTag();

			if (type == null)
				type = empty;

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
