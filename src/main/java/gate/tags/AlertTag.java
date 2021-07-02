package gate.tags;

import gate.base.Screen;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
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
			messages = ((Screen) getJspContext().findAttribute("screen")).getMessages();
		if (!messages.isEmpty())
		{
			String alerts = messages.stream()
				.map(e -> e.replace('\'', '"'))
				.map(e -> "alert('" + e + "');").collect(Collectors.joining());

			getJspContext().getOut().print("<script>");
			getJspContext().getOut().print("window.addEventListener('load',function(){");
			getJspContext().getOut().print(alerts);
			getJspContext().getOut().print("let href = window.location.href;");
			getJspContext().getOut().print("href = href.replace(/messages=[a-zA-Z0-9+\\/=%]*&/, '');");
			getJspContext().getOut().print("href = href.replace(/&messages=[a-zA-Z0-9+\\/=%]*/, '');");
			getJspContext().getOut().print("href = href.replace(/[?]messages=[a-zA-Z0-9+\\/=%]*/, '');");
			getJspContext().getOut().print("window.history.replaceState({}, document.title, href);");
			getJspContext().getOut().print("});");
			getJspContext().getOut().print("</script>");
		}
	}

	public void setMessages(List<String> messages)
	{
		this.messages = messages;
	}
}
