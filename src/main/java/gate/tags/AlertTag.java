package gate.tags;

import gate.base.Screen;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AlertTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		Screen screen = (Screen) getJspContext().findAttribute("screen");
		if (!screen.getMessages().isEmpty())
		{
			getJspContext().getOut().print("<script type='text/javascript'>window.addEventListener('load',function(){");
			for (String message : screen.getMessages())
				getJspContext().getOut().print(String.format("alert('%s');", message));
			getJspContext().getOut().print("});</script>");
		}
	}
}
