package gate.tags;

import gate.base.Screen;
import java.io.IOException;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AlertTag extends SimpleTagSupport
{

	private List<String> messages;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (messages == null)
			messages
				= ((Screen) getJspContext().findAttribute("screen"))
					.getMessages();
		if (!messages.isEmpty())
		{
			getJspContext().getOut().print("<script>window.addEventListener('load',function(){");
			for (String message : messages)
				getJspContext().getOut().print(String.format("alert('%s');", message));
			getJspContext().getOut().print("});</script>");
		}
	}

	public void setMessages(List<String> messages)
	{
		this.messages = messages;
	}
}
