package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class SourceCodeTag extends DynamicAttributeTag
{

	private String language;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspContext().getOut().println("<script type='text/plain' class='language-" + language + "'>");
		getJspBody().invoke(null);
		getJspContext().getOut().println("</script>");
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}
}
