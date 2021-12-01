package gate.thymeleaf.processors.tag;

import gate.thymeleaf.ELExpression;
import gate.util.Toolkit;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StackTraceProcessor extends TagAttributeProcessor
{

	@Inject
	ELExpression expression;

	public StackTraceProcessor()
	{
		super("stacktrace");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		Throwable exception = extract(element, handler, "exception")
			.map(expression::evaluate)
			.filter(e -> e instanceof Throwable)
			.map(e -> (Throwable) e)
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute exception on g:stacktrace"));

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<div title='Erro de sistema' data-popup>");
		string.add("<table><tbody>");
		for (Throwable error = exception; error != null; error = error.getCause())
		{
			string.add("<tr><td style='font-weight: bold'>");
			string.add(Toolkit.escapeHTML(error.toString()));
			string.add("</td></tr>");
			string.add("<tr><td>");
			string.add(Stream.of(error.getStackTrace()).map(StackTraceElement::toString)
				.collect(Collectors.joining("<br/>")));
			string.add("</td></tr>");
		}
		string.add("</tbody></table>");
		string.add("</div>");

		handler.replaceWith(string.toString(), false);
	}
}
