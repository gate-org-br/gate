package gate.tags.table;

import gate.annotation.Color;
import gate.annotation.Icon;
import gate.tags.AttributeTag;
import gate.util.Icons;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.jsp.JspException;

public class TITag extends AttributeTag
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
	public void doTag() throws JspException
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

			if (getParent() instanceof TRTag && ((TRTag) getParent()).getParent() instanceof THeadTag)
				getJspContext().getOut().print(getAttributes().isEmpty() ? "<th>" : "<th " + getAttributes() + ">");
			else
				getJspContext().getOut().print(getAttributes().isEmpty() ? "<td>" : "<td " + getAttributes() + ">");

			if (icon.getCode().length() == 1)
				getJspContext().getOut().print("<i c>" + icon + "</i>");
			else
				getJspContext().getOut().print("<i>" + icon + "</i>");

			if (getParent() instanceof TRTag && ((TRTag) getParent()).getParent() instanceof THeadTag)
				getJspContext().getOut().print("</th>");
			else
				getJspContext().getOut().print("</td>");

		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
