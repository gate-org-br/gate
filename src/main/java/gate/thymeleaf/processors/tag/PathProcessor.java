package gate.thymeleaf.processors.tag;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.type.Attributes;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PathProcessor extends TagProcessor
{

	public PathProcessor()
	{
		super("path");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		String module = extract(element, handler, "module").orElse(null);
		String screen = extract(element, handler, "screen").orElse(null);
		String action = extract(element, handler, "action").orElse(null);

		HttpServletRequest request = ((IWebContext) context).getRequest();

		boolean empty = module == null && screen == null && action == null;
		if ("#".equals(module) || empty)
			module = request.getParameter("MODULE");
		if ("#".equals(screen) || empty)
			screen = request.getParameter("SCREEN");
		if ("#".equals(action) || empty)
			action = request.getParameter("ACTION");

		StringJoiner string = new StringJoiner("");
		if (module != null)
		{
			Screen.getScreen(module, null).flatMap(PathProcessor::getLabel).ifPresent(string::add);
			if (screen != null)
			{
				Screen.getScreen(module, screen).flatMap(PathProcessor::getLabel).ifPresent(string::add);
				if (action != null)
					Screen.getAction(module, screen, action).flatMap(PathProcessor::getLabel).ifPresent(string::add);
			}
		}

		Attributes attributes = new Attributes();
		Stream.of(element.getAllAttributes())
			.filter(e -> e.getValue() != null)
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));
		handler.replaceWith("<g-path " + attributes + ">" + string + "</g-path>", false);
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
}
