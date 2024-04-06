package gate.tags.property;

import gate.util.Toolkit;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.LambdaExpression;
import jakarta.el.StandardELContext;

abstract class SelectorTag extends PropertyTag
{

	protected Iterable<?> options;
	protected LambdaExpression children;

	protected LambdaExpression values;
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

	public void setValues(LambdaExpression values)
	{
		this.values = values;
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
}
