package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpression;
import gate.util.Toolkit;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StackTraceAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public StackTraceAttributeProcessor()
	{
		super(null, "stacktrace");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		extract(element, handler, "g:stacktrace")
			.map(expression::evaluate)
			.filter(object -> object instanceof Throwable)
			.map(throwable -> (Throwable) throwable)
			.ifPresentOrElse(throwable ->
			{
				StringJoiner string = new StringJoiner(System.lineSeparator());
				string.add("<table><tbody>");
				for (Throwable exception = throwable;
					exception != null;
					exception = exception.getCause())
				{
					string.add("<tr><td style='font-weight: bold'>");
					string.add(Toolkit.escapeHTML(exception.toString()));
					string.add("</td></tr>");
					string.add("<tr><td>");
					string.add(Stream.of(exception.getStackTrace())
						.map(StackTraceElement::toString)
						.collect(Collectors.joining("<br/>")));
					string.add("</td></tr>");
				}
				string.add("</tbody></table>");

				handler.setBody(string.toString(), false);
			}, handler::removeElement);
	}
}
