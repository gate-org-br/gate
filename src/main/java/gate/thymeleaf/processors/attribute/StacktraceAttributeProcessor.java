package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpression;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StacktraceAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public StacktraceAttributeProcessor()
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
				string.add("<ul class='TreeView'>");
				for (Throwable error = throwable; error != null; error = error.getCause())
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

				handler.setBody(string.toString(), false);
			}, handler::removeElement);
	}
}
