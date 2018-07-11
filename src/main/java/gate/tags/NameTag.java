package gate.tags;

import gate.error.AppError;
import gate.annotation.Name;
import java.io.IOException;

import javax.servlet.jsp.JspException;

public class NameTag extends DynamicAttributeTag
{

	private String type;

	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		try
		{
			super.doTag();
			try
			{
				Class clazz = Class.forName(this.type);

				if (!clazz.isAnnotationPresent(Name.class))
					getJspContext().getOut().print(String.format("no name defined for %s", type));
				else
					getJspContext().getOut().print(((Name) clazz.getAnnotation(Name.class)).value());
			} catch (ClassNotFoundException e)
			{
				getJspContext().getOut().print(String.format("%s is not a valid class name", type));
			}
		} catch (JspException | IOException e)
		{
			throw new AppError(e);
		}
	}
}
