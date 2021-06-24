package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class SecureTag extends AccessTag
{

	private String otherwise;

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (checkAccess())
			getJspBody().invoke(null);
		else if (otherwise != null)
			getJspContext().getOut().print(otherwise);

	}
}
