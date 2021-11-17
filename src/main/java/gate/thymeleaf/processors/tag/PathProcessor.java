package gate.thymeleaf.processors.tag;

import gate.annotation.Icon;
import gate.annotation.Name;
import gate.base.Screen;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PathProcessor extends ModelProcessor
{

	public PathProcessor()
	{
		super("path");
	}

	@Override
	protected void doProcess(Model model)
	{
		String module = model.get("module");
		String screen = model.get("screen");
		String action = model.get("action");
		boolean empty = module == null && screen == null && action == null;
		if ("#".equals(module) || empty)
			module = model.request().getParameter("MODULE");
		if ("#".equals(screen) || empty)
			screen = model.request().getParameter("SCREEN");
		if ("#".equals(action) || empty)
			action = model.request().getParameter("ACTION");

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
		model.stream()
			.filter(e -> e.getValue() != null)
			.filter(e -> !e.getAttributeCompleteName().startsWith("_"))
			.filter(e -> !"module".equals(e.getAttributeCompleteName()))
			.filter(e -> !"screen".equals(e.getAttributeCompleteName()))
			.filter(e -> !"action".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));
		model.replaceAll("<g-path " + attributes + ">" + string + "</g-path>");
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
