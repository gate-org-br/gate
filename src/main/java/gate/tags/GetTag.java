package gate.tags;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class GetTag extends ParameterTag
{

	private String otherwise;

	@Inject
	private HttpServletRequest request;

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if ("GET".equalsIgnoreCase(request.getMethod()))
		{
			getJspBody().invoke(null);
		} else if (otherwise != null)
		{
			getJspContext().getOut().print("<div class='TEXT'>");
			getJspContext().getOut().print("<h1>" + otherwise + "</h1>");
			getJspContext().getOut().print("</div>");
		}
	}
}
