package gate.tags.template;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

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
	public void doTag() throws JspException, IOException
	{
		try
		{
			Writer writer = new CharArrayWriter();
			getJspBody().invoke(writer);
			getJspContext().setAttribute("body", writer.toString(), PageContext.REQUEST_SCOPE);
			((PageContext) getJspContext()).include(filename);
		} catch (ServletException ex)
		{
			throw new IOException(ex);
		}
		super.doTag();
	}
}
