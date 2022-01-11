package gate.thymeleaf.processors.tag;

import gate.thymeleaf.ELExpression;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StacktraceProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public StacktraceProcessor()
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
		string.add("<ul class='TreeView'>");
		for (Throwable error = exception; error != null; error = error.getCause())
		{
			string.add("<li>");
			string.add(error.getMessage());
			string.add("<ul>");
			Stream.of(error.getStackTrace())
				.map(StackTraceElement::toString)
				.forEach(e -> string.add("<li>").add(e).add("</li>"));
			string.add("</ul>");
			string.add("</li>");
		}
		string.add("</ul>");

		handler.replaceWith(string.toString(), false);
	}
}
