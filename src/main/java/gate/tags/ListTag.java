package gate.tags;

import gate.converter.Converter;
import gate.util.Toolkit;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.LambdaExpression;
import jakarta.el.StandardELContext;
import jakarta.servlet.jsp.JspException;
import java.io.IOException;
import java.util.stream.Collectors;

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

			for (var group : Toolkit.collection(options).stream()
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
