package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class OtherwiseTag extends SimpleTagSupport
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (!(getParent() instanceof ChooseTag))
			throw new IOException("The Otherwise tag must be inside a Choose tag");

		if (((ChooseTag) getParent()).getOtherwiseTag() != null)
			throw new IOException("Multiple Otherwise tags found on Choose tag");

		((ChooseTag) getParent()).setOtherwiseTag(this);
	}

	public void invoke() throws JspException, IOException
	{
		getJspBody().invoke(null);
	}
}
