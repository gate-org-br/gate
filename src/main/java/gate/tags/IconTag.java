package gate.tags;

import gate.error.AppError;
import gate.util.Icons;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class IconTag extends DynamicAttributeTag
{

	private Object type;
	private String field;

	public void setType(Object type)
	{
		this.type = type;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		try
		{
			super.doTag();
			Icons.Icon icon = Icons.getInstance().get(type, field);
			if (icon == null)
				getJspContext().getOut().print(String.format("<span %s>%s</span>", getAttributes().toString(), "??"));
			else if (icon.getCode().length() == 1)
				getJspContext().getOut().print(String.format("<span %s>%s</span>", getAttributes().toString(), icon.getCode()));
			else
				getJspContext().getOut().print(String.format("<i %s>&#x%s;</i>", getAttributes().toString(), icon.getCode()));
		} catch (JspException | IOException e)
		{
			throw new AppError(e);
		}
	}
}
