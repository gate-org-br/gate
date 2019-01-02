package gate.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class DeniedTag extends AccessTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		if (!check())
			getJspBody().invoke(null);
		else if (otherwise != null)
			getJspContext().getOut().print(otherwise);
	}
}
