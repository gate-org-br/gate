package gate.tags;

import gate.Gate;
import gate.annotation.Asynchronous;
import gate.annotation.Current;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.entity.User;
import gate.io.URL;
import gate.util.Icons;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class AnchorTag extends DynamicAttributeTag
{

	@Inject
	@Current
	private User user;

	private String module;

	private String screen;

	private String action;

	private String target;

	private String method;

	private Integer tabindex;

	private String arguments;
	private boolean condition = true;
	private Class<Screen> javaClass;
	private Method javaMethod;

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

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public String getModule()
	{
		return module;
	}

	public String getScreen()
	{
		return screen;
	}

	public String getAction()
	{
		return action;
	}

	public void setCondition(boolean condition)
	{
		this.condition = condition;
	}

	public boolean getCondition()
	{
		return condition;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setArguments(String arguments)
	{
		this.arguments = arguments;
	}

	public String getMethod()
	{
		return method;
	}

	public String getArguments()
	{
		return arguments;
	}

	public void setTabindex(Integer tabindex)
	{
		this.tabindex = tabindex;
	}

	public Integer getTabindex()
	{
		return tabindex;
	}

	public Class<Screen> getJavaClass()
	{
		return javaClass;
	}

	public Method getJavaMethod()
	{
		return javaMethod;
	}

	public boolean checkAccess()
	{
		return Gate.checkAccess(user, module, screen, action, javaClass, javaMethod);
	}

	public String getURL()
	{
		return URL.toString(module, screen, action, arguments);
	}

	public Icons.Icon getIcon()
	{
		return Icon.Extractor.extract(javaMethod).or(() -> Icon.Extractor.extract(javaClass)).orElse(Icons.UNKNOWN);
	}

	public String getName()
	{
		return Name.Extractor.extract(javaMethod).or(() -> Name.Extractor.extract(javaClass)).orElse("unnamed");
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		if (Toolkit.isEmpty(module)
			&& Toolkit.isEmpty(screen)
			&& Toolkit.isEmpty(action))
			return;

		PageContext pageContext = (PageContext) getJspContext();

		module = "#".equals(module) ? pageContext.getRequest().getParameter("MODULE") : module;
		screen = "#".equals(screen) ? pageContext.getRequest().getParameter("SCREEN") : screen;
		action = "#".equals(action) ? pageContext.getRequest().getParameter("ACTION") : action;

		javaClass = Screen.getScreen(getModule(), getScreen()).orElseThrow(() -> new JspException(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s", module, screen, action)));
		javaMethod = Screen.getAction(getJavaClass(), getAction()).orElseThrow(() -> new JspException(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s", module, screen, action)));

		if (javaMethod.isAnnotationPresent(Asynchronous.class))
			if ("_dialog".equals(target))
				target = "_progress-dialog";
			else
				target = "_progress-window";

	}

}
