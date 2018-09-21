package gate.tags;

import gate.Gate;
import gate.annotation.Current;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.User;
import gate.io.URL;
import gate.type.Attributes;
import gate.util.Icons;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class MenuLinkTag extends DynamicAttributeTag
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private String arguments;

	private String method;
	private String target;

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

			if (Toolkit.isEmpty(module)
					&& Toolkit.isEmpty(screen)
					&& Toolkit.isEmpty(action))
			{
				Attributes attributes = new Attributes();
				attributes.put("href", "Gate");
				if (!attributes.containsKey("title"))
					getAttributes().put("title", "Sair do sistema");

				pageContext.getOut().print("<li class='MenuLink' " + getAttributes() + ">");
				pageContext.getOut().print("<a " + attributes + ">");

				if (getJspBody() != null)
					getJspBody().invoke(null);
				else
					pageContext.getOut().print("<i>&#X2007;</i>");

				pageContext.getOut().print("</a>");
				pageContext.getOut().print("</li>");
			} else
			{
				if ("#".equals(module))
					module = pageContext.getRequest().getParameter("MODULE");
				if ("#".equals(screen))
					screen = pageContext.getRequest().getParameter("SCREEN");
				if ("#".equals(action))
					action = pageContext.getRequest().getParameter("ACTION");

				Class<Screen> clazz = Screen.getScreen(module, screen)
						.orElseThrow(() -> new JspException(String.format(
						"Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
						module, screen, action)));
				Method method = Screen.getAction(clazz, action)
						.orElseThrow(() -> new JspException(String.format(
						"Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
						module, screen, action)));

				if (Gate.checkAccess(user, module, screen, action, clazz, method))
				{
					String title = "unnamed";
					if (method.isAnnotationPresent(Name.class))
						title = method.getAnnotation(Name.class).value();
					else if (clazz.isAnnotationPresent(Name.class))
						title = clazz.getAnnotation(Name.class).value();
					if (!getAttributes().containsKey("title"))
						getAttributes().put("title", title);

					pageContext.getOut().print("<li class='MenuLink' " + getAttributes() + ">");
					if ("POST".equalsIgnoreCase(this.method))
					{
						Attributes attribute = new Attributes();
						if (target != null)
							attribute.put("formtarget", target);
						attribute.put("formaction", URL.toString(module, screen, action, arguments));
						pageContext.getOut().print("<button " + attribute + ">");
						if (getJspBody() != null)
							getJspBody().invoke(null);
						else
							pageContext.getOut().print(createBody(clazz, method));
						pageContext.getOut().print("</button>");
					} else
					{
						Attributes attribute = new Attributes();
						if (target != null)
							attribute.put("target", target);
						attribute.put("href", URL.toString(module, screen, action, arguments));
						pageContext.getOut().print("<a " + attribute + ">");
						if (getJspBody() != null)
							getJspBody().invoke(null);
						else
							pageContext.getOut().print(createBody(clazz, method));
						pageContext.getOut().print("</a>");
					}
					pageContext.getOut().print("</li>");
				}
			}
		} catch (SecurityException ex)
		{
			throw new JspException(ex);
		}
	}

	private String createBody(Class<?> clazz, Method method)
	{
		Icons.Icon icon = Icons.getInstance().get(method, null);
		if (icon == Icons.UNKNOWN)
			icon = Icons.getInstance().get(clazz, null);
		return String.format("<i>&#X%s;</i>", icon.getCode());
	}
}
