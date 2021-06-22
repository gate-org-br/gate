package gate.tags;

import gate.Gate;
import gate.annotation.Asynchronous;
import gate.annotation.Color;
import gate.annotation.Current;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Tooltip;
import gate.base.Screen;
import gate.entity.User;
import gate.error.ConversionException;
import gate.io.URL;
import gate.util.Icons;
import gate.util.Parameters;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.StringJoiner;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public abstract class AnchorTag extends ParameterTag
{

	@Inject
	@Current
	protected User user;

	protected String otherwise;

	protected String module;

	protected String screen;

	protected String action;

	protected String target;

	protected String method;

	protected Integer tabindex;

	protected boolean condition = true;
	protected Class<Screen> javaClass;
	protected Method javaMethod;

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

	public boolean checkAccess()
	{
		return Gate.checkAccess(user, module, screen, action, javaClass, javaMethod);
	}

	public boolean checkAccess(String module, String screen, String action)
	{
		return Gate.checkAccess(user, module, screen, action, javaClass, javaMethod);
	}

	public String getURL()
	{
		return URL.toString(module, screen, action, getParameters().toString());
	}

	public Optional<Icons.Icon> getIcon()
	{
		return action != null ? Icon.Extractor.extract(javaMethod)
			: Icon.Extractor.extract(javaMethod).or(() -> Icon.Extractor.extract(javaClass));
	}

	public Optional<String> getName()
	{
		return action != null ? Name.Extractor.extract(javaMethod)
			: Name.Extractor.extract(javaMethod).or(() -> Name.Extractor.extract(javaClass));
	}

	public Optional<String> getDescription()
	{
		return action != null ? Description.Extractor.extract(javaMethod)
			: Description.Extractor.extract(javaMethod).or(() -> Description.Extractor.extract(javaClass));
	}

	public Optional<String> getTooltip()
	{
		return action != null ? Tooltip.Extractor.extract(javaMethod)
			: Tooltip.Extractor.extract(javaMethod).or(() -> Tooltip.Extractor.extract(javaClass));
	}

	public Optional<String> getColor()
	{
		return action != null ? Color.Extractor.extract(javaMethod)
			: Color.Extractor.extract(javaMethod).or(() -> Color.Extractor.extract(javaClass));
	}

	public String createBody()
	{
		StringJoiner string = new StringJoiner("").setEmptyValue("unamed");
		getName().ifPresent(string::add);
		getIcon().ifPresent(e -> string.add("<i>&#X" + e.getCode() + ";</i>"));
		return string.toString();
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (!Toolkit.isEmpty(method)
			|| !Toolkit.isEmpty(module)
			|| !Toolkit.isEmpty(screen)
			|| !Toolkit.isEmpty(action))
		{
			PageContext pageContext = (PageContext) getJspContext();

			if (Toolkit.isEmpty(module))
			{
				module = pageContext.getRequest().getParameter("MODULE");
				if (Toolkit.isEmpty(screen))
				{
					screen = pageContext.getRequest().getParameter("SCREEN");
					if (Toolkit.isEmpty(action))
						action = pageContext.getRequest().getParameter("ACTION");
				}
			}

			if ("#".equals(module))
				module = pageContext.getRequest().getParameter("MODULE");
			if ("#".equals(screen))
				screen = pageContext.getRequest().getParameter("SCREEN");
			if ("#".equals(action))
				action = pageContext.getRequest().getParameter("ACTION");

			javaClass = Screen.getScreen(module, screen).orElseThrow(() -> new JspException(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s", module, screen, action)));
			javaMethod = Screen.getAction(javaClass, action).orElseThrow(() -> new JspException(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s", module, screen, action)));

			if (javaMethod.isAnnotationPresent(Asynchronous.class))
				if ("_dialog".equals(target))
					target = "_progress-dialog";
				else
					target = "_progress-window";

			if (!getAttributes().containsKey("style"))
				getColor().ifPresent(e -> getAttributes().put("style", "color: " + e));

			if (!getAttributes().containsKey("data-tooltip"))
				getTooltip().ifPresent(e -> getAttributes().put("data-tooltip", e));

			if (!getAttributes().containsKey("title"))
				getDescription().ifPresent(e -> getAttributes().put("title", e));

			if (!getAttributes().containsKey("title"))
				getName().ifPresent(e -> getAttributes().put("title", e));

			if (condition)
				if (checkAccess())
					if ("POST".equalsIgnoreCase(method))
						post();
					else
						get();
				else
					accessDenied();
			else if (otherwise != null)
				otherwise();

		} else
			exit();
	}

	public void get() throws JspException, IOException
	{

	}

	public void post() throws JspException, IOException
	{

	}

	public void exit() throws JspException, IOException
	{

	}

	public void otherwise() throws JspException, IOException
	{

	}

	public void accessDenied() throws JspException, IOException
	{

	}

}
