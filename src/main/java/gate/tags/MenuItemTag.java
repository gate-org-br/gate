package gate.tags;

import gate.Gate;
import gate.annotation.Current;
import gate.annotation.Description;
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

public class MenuItemTag extends DynamicAttributeTag
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

	private Integer tabindex;
	private boolean fixed = false;

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

	public void setFixed(boolean fixed)
	{
		this.fixed = fixed;
	}

	public void setTabindex(Integer tabindex)
	{
		this.tabindex = tabindex;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		PageContext pageContext = (PageContext) getJspContext();
		if (Toolkit.isEmpty(module)
			&& Toolkit.isEmpty(screen)
			&& Toolkit.isEmpty(action))
		{
			pageContext.getOut().print("<li " + getAttributes() + ">");

			Attributes atrributes = new Attributes();
			if (target != null)
				atrributes.put("target", target);
			if (tabindex != null)
				atrributes.put("tabindex", tabindex);

			atrributes.put("href", "Gate");
			pageContext.getOut().print("<a " + atrributes + ">");
			pageContext.getOut().print("Sair do sistema<i>&#X2007;</i>");
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
				.orElseThrow(() -> new IOException(String.format(
				"Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
				module, screen, action)));
			Method method = Screen.getAction(clazz, action)
				.orElseThrow(() -> new IOException(String.format(
				"Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
				module, screen, action)));

			if (!getAttributes().containsKey("title")
				&& method.isAnnotationPresent(Description.class))
				getAttributes().put("title", method.getAnnotation(Description.class).value());

			if (Gate.checkAccess(user,
				module, screen, action, clazz, method))
			{
				pageContext.getOut().print("<li " + getAttributes() + ">");
				if ("POST".equalsIgnoreCase(this.method))
				{
					Attributes atrributes = new Attributes();
					if (target != null)
						atrributes.put("formtarget", target);
					if (tabindex != null)
						atrributes.put("tabindex", tabindex);

					atrributes.put("formaction", URL.toString(module, screen, action, arguments));
					pageContext.getOut().print("<button " + atrributes + ">");
					if (getJspBody() != null)
						getJspBody().invoke(null);
					else
						pageContext.getOut().print(createBody(clazz, method));
					pageContext.getOut().print("</button>");
				} else
				{
					Attributes atrributes = new Attributes();
					if (target != null)
						atrributes.put("target", target);
					if (tabindex != null)
						atrributes.put("tabindex", tabindex);

					atrributes.put("href", URL.toString(module, screen, action, arguments));
					pageContext.getOut().print("<a " + atrributes + ">");
					if (getJspBody() != null)
						getJspBody().invoke(null);
					else
						pageContext.getOut().print(createBody(clazz, method));
					pageContext.getOut().print("</a>");
				}
				pageContext.getOut().print("</li>");
			} else if (fixed)
			{
				pageContext.getOut().print("<li " + getAttributes() + ">");

				Attributes atrributes = new Attributes();
				atrributes.put("href", "#");
				atrributes.put("data-disabled", "true");
				if (tabindex != null)
					atrributes.put("tabindex", tabindex);
				pageContext.getOut().print("<a " + atrributes + ">");
				if (getJspBody() != null)
					getJspBody().invoke(null);
				else
					pageContext.getOut().print(createBody(clazz, method));
				pageContext.getOut().print("</a>");
				pageContext.getOut().print("</li>");
			}
		}
	}

	private String createBody(Class<?> clazz, Method method)
	{
		Icons.Icon icon = Icons
			.getInstance().get(method);
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
