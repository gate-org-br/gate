package gate.thymeleaf.processors.tag;

import gate.base.Screen;
import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.util.Toolkit;
import java.util.List;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class AlertProcessor extends TagProcessor
{

	@Inject
	private ELExpression expression;

	public AlertProcessor()
	{
		super("alert");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		List<? extends Object> messages = extract(element, handler, "messages")
			.map(expression::evaluate)
			.map(Toolkit::list)
			.orElseGet(() ->
			{
				IWebContext webContext = (IWebContext) context;
				HttpServletRequest request = webContext.getRequest();
				Screen screen = (Screen) request.getAttribute("screen");
				return screen != null ? screen.getMessages() : List.of();
			});

		if (!messages.isEmpty())
		{
			StringJoiner string = new StringJoiner(System.lineSeparator());

			string.add("<script>");
			string.add("window.addEventListener('load',function(){");
			messages.stream()
				.map(Converter::toText)
				.map(e -> e.replace('\'', '"'))
				.map(e -> "alert('" + e + "');")
				.forEach(string::add);
			string.add("let href = window.location.href;");
			string.add("href = href.replace(/messages=[a-zA-Z0-9+\\/=%]*&/, '');");
			string.add("href = href.replace(/&messages=[a-zA-Z0-9+\\/=%]*/, '');");
			string.add("href = href.replace(/[?]messages=[a-zA-Z0-9+\\/=%]*/, '');");
			string.add("window.history.replaceState({}, document.title, href);");
			string.add("});");
			string.add("</script>");

			handler.replaceWith(string.toString(), false);
		} else
			handler.removeElement();
	}
}
