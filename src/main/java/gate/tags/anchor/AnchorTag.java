package gate.tags.anchor;

import gate.Call;
import gate.annotation.Asynchronous;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.ConversionException;
import gate.io.URL;
import gate.tags.ParameterTag;
import gate.util.Parameters;
import java.io.IOException;
import java.util.StringJoiner;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public abstract class AnchorTag extends ParameterTag
{

	@Inject
	@Current
	protected User user;

	@Inject
	private HttpServletRequest request;

	protected String otherwise;

	protected String module;

	protected String screen;

	protected String action;

	protected String target;

	protected String method;

	protected Integer tabindex;

	protected Call call;

	protected boolean condition = true;

	public void setModule(String module)
	{
		this.module = module;
	}

	public void setScreen(String screen)
	{
		this.screen = screen;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public void setCondition(boolean condition)
	{
		this.condition = condition;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	public void setArguments(String arguments) throws ConversionException
	{
		getParameters().putAll(Parameters.parse(arguments));
	}

	public void setTabindex(Integer tabindex)
	{
		this.tabindex = tabindex;
	}

	public String getURL()
	{
		return URL.toString(call.getModule(),
			call.getScreen(),
			call.getAction(),
			getParameters().toString());
	}

	public String createBody()
	{
		StringJoiner string = new StringJoiner("").setEmptyValue("unamed");
		call.getName().ifPresent(string::add);

		call.getIcon().map(e -> "<i>" + e + "</i>")
			.or(() -> call.getEmoji().map(e -> "<e>" + e + "</e>"))
			.ifPresent(string::add);
		return string.toString();
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		call = Call.of(request, module, screen, action);

		if (call.getMethod().isAnnotationPresent(Asynchronous.class))
			target = "_progress-dialog";

		if (!getAttributes().containsKey("style"))
			call.getColor().ifPresent(e -> getAttributes().put("style", "color: " + e));

		if (!getAttributes().containsKey("data-tooltip"))
			call.getTooltip().ifPresent(e -> getAttributes().put("data-tooltip", e));

		if (!getAttributes().containsKey("data-confirm"))
			call.getConfirm().ifPresent(e -> getAttributes().put("data-confirm", e));

		if (!getAttributes().containsKey("data-alert"))
			call.getAlert().ifPresent(e -> getAttributes().put("data-alert", e));

		if (!getAttributes().containsKey("title"))
			call.getDescription().ifPresent(e -> getAttributes().put("title", e));

		if (!getAttributes().containsKey("title"))
			call.getName().ifPresent(e -> getAttributes().put("title", e));

		if (condition)
			if (call.checkAccess(user))
				if ("POST".equalsIgnoreCase(method))
					post();
				else
					get();
			else
				accessDenied();
		else if (otherwise != null)
			otherwise();
	}

	public void get() throws JspException, IOException
	{

	}

	public void post() throws JspException, IOException
	{

	}

	public void otherwise() throws JspException, IOException
	{

	}

	public void accessDenied() throws JspException, IOException
	{

	}

}
