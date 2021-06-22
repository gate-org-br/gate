package gate.tags;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.StringJoiner;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class PathTag extends AttributeTag
{

	private String module;
	private String screen;
	private String action;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		PageContext pageContext = (PageContext) getJspContext();

		boolean empty = module == null && screen == null && action == null;
		if ("#".equals(module) || empty)
			module = pageContext.getRequest().getParameter("MODULE");
		if ("#".equals(screen) || empty)
			screen = pageContext.getRequest().getParameter("SCREEN");
		if ("#".equals(action) || empty)
			action = pageContext.getRequest().getParameter("ACTION");

		StringJoiner string = new StringJoiner("");

		if (module != null)
		{
			Screen.getScreen(module, null).flatMap(PathTag::getLabel).ifPresent(string::add);
			if (screen != null)
			{
				Screen.getScreen(module, screen).flatMap(PathTag::getLabel).ifPresent(string::add);
				if (action != null)
					Screen.getAction(module, screen, action).flatMap(PathTag::getLabel).ifPresent(string::add);
			}
		}

		pageContext.getOut().print("<g-path " + getAttributes() + ">");
		pageContext.getOut().print(string.toString());
		pageContext.getOut().print("</g-path>");
	}

	private static Optional<String> getLabel(AnnotatedElement element)
	{
		return getText(element).map(e -> "<label>" + e + "</label>");
	}

	private static Optional<String> getText(AnnotatedElement element)
	{
		StringJoiner string = new StringJoiner("");
		Name.Extractor.extract(element).ifPresent(string::add);
		Icon.Extractor.extract(element).ifPresent(e -> string.add("<i>&#X" + e.getCode() + ";</i>"));
		return string.length() != 0 ? Optional.of(string.toString()) : Optional.empty();
	}

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
}
