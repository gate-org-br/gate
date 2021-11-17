package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ChildTag extends SimpleTagSupport
{

	private Object source;

	public void setSource(Object source)
	{
		this.source = source;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		ParentTag parent = getParent(this);
		if (parent == null)
			throw new IOException("a child tag must be inside a parent tag");
		Object value = parent.getSource();
		parent.setSource(this.source);
		parent.doTag();
		parent.setSource(value);
	}

	private ParentTag getParent(SimpleTag simpleTag)
	{
		return simpleTag.getParent() instanceof ParentTag
			? (ParentTag) simpleTag.getParent()
			: simpleTag.getParent() instanceof SimpleTag
			? getParent((SimpleTag) simpleTag.getParent()) : null;
	}
}
