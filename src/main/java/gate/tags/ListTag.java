package gate.tags;

import gate.converter.Converter;
import gate.util.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.LambdaExpression;
import javax.el.StandardELContext;
import javax.servlet.jsp.JspException;

public class ListTag extends AttributeTag
{

	protected Iterable<?> options;
	protected LambdaExpression children;

	protected LambdaExpression labels;
	protected LambdaExpression groups;
	protected LambdaExpression sortby;

	protected final ELContext EL_CONTEXT
		= new StandardELContext(ExpressionFactory.newInstance());

	public void setOptions(Object options)
	{
		this.options = Toolkit.iterable(options);
	}

	public void setLabels(LambdaExpression labels)
	{
		this.labels = labels;
	}

	public void setGroups(LambdaExpression groups)
	{
		this.groups = groups;
	}

	public void setSortby(LambdaExpression sortby)
	{
		this.sortby = sortby;
	}

	public void setChildren(LambdaExpression children)
	{
		this.children = children;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (options == null)
			throw new IOException("No option defined");

		if (sortby != null)
			options = Toolkit.collection(options)
				.stream()
				.sorted((a, b) -> (Integer) sortby.invoke(EL_CONTEXT, a, b))
				.collect(Collectors.toList());

		if (groups != null)
		{
			getJspContext().getOut().print(String.format("<ul %s>", getAttributes()));

			for (Map.Entry<Object, List<Object>> group : Toolkit.collection(options).stream()
				.collect(Collectors.groupingBy(e -> groups.invoke(EL_CONTEXT, e), Collectors.toList())).entrySet())
			{
				getJspContext().getOut().print("<li>");
				getJspContext().getOut().print(Converter.toText(group.getKey()));
				print(group.getValue());
				getJspContext().getOut().print("</li>");
			}

			getJspContext().getOut().print("</ul>");
		} else
			print(options);
	}

	private void print(Iterable<?> options) throws IOException, JspException
	{
		getJspContext().getOut().print("<ul>");

		for (Object option : options)
		{
			getJspContext().getOut().print("<li>");
			getJspContext().getOut().print("<label>");

			if (getJspBody() != null)
			{
				getJspContext().setAttribute("option", option);
				getJspBody().invoke(null);
				getJspContext().removeAttribute("option");
			} else if (labels != null)
				getJspContext().getOut()
					.print(Converter.toText(labels.invoke(EL_CONTEXT, option)));
			else
				getJspContext().getOut()
					.print(Converter.toText(option));

			getJspContext().getOut().print("</label>");

			if (children != null)
				print(Toolkit.iterable(children.invoke(EL_CONTEXT, option)));

			getJspContext().getOut().print("</li>");
		}
		getJspContext().getOut().print("</ul>");
	}

}
