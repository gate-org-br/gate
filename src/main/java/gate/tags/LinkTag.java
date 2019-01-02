package gate.tags;

import gate.Gate;
import gate.annotation.Current;
import gate.annotation.Description;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.User;
import gate.io.URL;
import gate.util.Icons;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class LinkTag extends DynamicAttributeTag
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private String arguments;

	private String otherwise;

	private String method;
	private String target;

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	public void setArguments(String arguments)
	{
		this.arguments = arguments;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public void setModule(String module)
	{
		this.module = module;
	}

	public void setScreen(String screen)
	{
		this.screen = screen;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		try
		{
			PageContext pageContext = (PageContext) getJspContext();

			if ("#".equals(module))
				module = pageContext.getRequest().getParameter("MODULE");
			if ("#".equals(screen))
				screen = pageContext.getRequest().getParameter("SCREEN");
			if ("#".equals(action))
				action = pageContext.getRequest().getParameter("ACTION");

			Class<Screen> clazz = Screen.getScreen(module, screen)
				.orElseThrow(() -> new JspException(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s", module, screen, action)));
			Method method = Screen.getAction(clazz, action)
				.orElseThrow(() -> new JspException(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s", module, screen, action)));

			if (!getAttributes().containsKey("title") && method.isAnnotationPresent(Description.class))
				getAttributes().put("title", method.getAnnotation(Description.class).value());

			if (Gate.checkAccess(user, module, screen, action, clazz, method))
			{
				if ("POST".equalsIgnoreCase(this.method))
				{
					if (target != null)
						getAttributes().put("formtarget", target);
					getAttributes().put("formaction", URL.toString(module, screen, action, arguments));
					pageContext.getOut().print("<button " + getAttributes() + ">");
					if (getJspBody() != null)
						getJspBody().invoke(null);
					else
						pageContext.getOut().print(createBody(clazz, method));
					pageContext.getOut().print("</button>");
				} else
				{
					if (target != null)
						getAttributes().put("target", target);
					getAttributes().put("href", URL.toString(module, screen, action, arguments));
					pageContext.getOut().print("<a " + getAttributes() + ">");
					if (getJspBody() != null)
						getJspBody().invoke(null);
					else
						pageContext.getOut().print(createBody(clazz, method));
					pageContext.getOut().print("</a>");
				}

			} else if (otherwise != null)
				pageContext.getOut().print(otherwise);
		} catch (SecurityException ex)
		{
			throw new JspException(ex);
		}
	}

	private String createBody(Class<?> clazz, Method method)
	{
		Icons.Icon icon = Icons.getInstance().get(method);
		if (icon == Icons.UNKNOWN)
			icon = Icons.getInstance().get(clazz);

		String name = "unnamed";
		if (method.isAnnotationPresent(Name.class))
			name = method.getAnnotation(Name.class).value();
		else if (clazz.isAnnotationPresent(Name.class))
			name = clazz.getAnnotation(Name.class).value();

		return String.format("%s<i>&#X%s;</i>", name, icon.getCode());
	}

}
