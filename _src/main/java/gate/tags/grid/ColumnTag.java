package gate.tags.grid;

import gate.converter.Converter;
import gate.lang.property.Property;
import gate.tags.DynamicAttributeTag;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class ColumnTag extends DynamicAttributeTag
{

	private String size;
	private Object name;
	private String property;
	private Object foot;
	private Align align;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (getParent() instanceof GridTag)
		{
			GridTag gridTag = (GridTag) getParent();
			gridTag.getCols().add(this);
		} else
			throw new IOException("The Column tag must be inside a Grid tag");
	}

	public void print(Object obj) throws IOException, JspException
	{
		Property p = Property.getProperty(obj.getClass(), getProperty());
		getJspContext().getOut().print(String.format("<td style='text-align: %s'>", getAlign().name()));
		if (getJspBody() != null)
		{
			getJspContext().setAttribute("value", Converter.toText(p.getValue(obj)));
			getJspBody().invoke(getJspContext().getOut());
			getJspContext().removeAttribute("value");
		} else
			getJspContext().getOut().print(Converter.toText(p.getValue(obj)));
		getJspContext().getOut().print("</td>");
	}

	public Object getName()
	{
		return name;
	}

	public void setName(Object head)
	{
		this.name = head;
	}

	public String getProperty()
	{
		return property;
	}

	public void setProperty(String body)
	{
		this.property = body;
	}

	public Object getFoot()
	{
		return foot;
	}

	public void setFoot(Object foot)
	{
		this.foot = foot;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public Align getAlign()
	{
		if (align == null)
			align = Align.left;
		return align;
	}

	public void setAlign(Align align)
	{
		this.align = align;
	}

	public enum Align
	{
		center, left, right, justify
	}
}
