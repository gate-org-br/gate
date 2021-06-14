package gate.tags.table;

import gate.annotation.Color;
import gate.annotation.Icon;
import gate.error.AppError;
import gate.tags.DynamicAttributeTag;
import gate.util.Icons;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TITag extends DynamicAttributeTag
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

			if (!getAttributes().containsKey("style"))
				Color.Extractor.extract(type)
					.ifPresent(e -> getAttributes().put("style", "color: " + e));

			Icons.Icon icon = Icon.Extractor.extract(type != null ? type : empty).orElse(Icons.UNKNOWN);

			if (getParent() instanceof THeadTag)
				getJspContext().getOut().print(getAttributes().isEmpty() ? "<th>" : "<th " + getAttributes() + ">");
			else
				getJspContext().getOut().print(getAttributes().isEmpty() ? "<td>" : "<td " + getAttributes() + ">");

			if (icon.getCode().length() == 1)
				getJspContext().getOut().print(String.format("<i c>" + icon.getCode() + "</i'>"));
			else
				getJspContext().getOut().print(String.format("<i>" + icon.getCode() + "</i'>"));

			if (getParent() instanceof THeadTag)
				getJspContext().getOut().print("</th>");
			else
				getJspContext().getOut().print("</td>");

		} catch (JspException | IOException e)
		{
			throw new AppError(e);
		}
	}
}
