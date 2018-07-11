package gate.tags;

import gate.lang.dataurl.DataURL;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class ImgTag extends DynamicAttributeTag
{

	private DataURL source;

	public void setSource(DataURL source)
	{
		this.source = source;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (source != null)
		{
			String string = source.getData();
			getAttributes().put("src", String.format("data:image/%s;base64,%s", source.getType(), string));
			getJspContext().getOut().print(String.format("<img %s/>", getAttributes().toString()));
		}
	}
}
