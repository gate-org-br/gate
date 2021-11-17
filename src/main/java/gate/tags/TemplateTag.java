package gate.tags;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.slf4j.LoggerFactory;

public class TemplateTag extends SimpleTagSupport
{

	private String filename;

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	@Override
	public void doTag()
	{
		try
		{
			Writer writer = new CharArrayWriter();
			getJspBody().invoke(writer);
			getJspContext().setAttribute("g-template-content", writer.toString(), PageContext.REQUEST_SCOPE);
			((PageContext) getJspContext()).include(filename);
			super.doTag();
		} catch (ServletException | JspException | IOException ex)
		{
			try
			{
				getJspContext().getOut().print("<ul>");

				for (Throwable error = ex;
					error != null;
					error = error.getCause())
				{
					getJspContext().getOut().println("<li>");
					getJspContext().getOut().println("<strong>" + error.toString() + "</strong>");
					getJspContext().getOut().print("<ul>");
					for (StackTraceElement element : error.getStackTrace())
						getJspContext().getOut().println("<li>" + element.toString() + "</li>");
					getJspContext().getOut().print("</ul>");
					getJspContext().getOut().println("</li>");
				}

				getJspContext().getOut().print("</ul>");

			} catch (IOException ex1)
			{
				LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
			}
		}
	}
}
