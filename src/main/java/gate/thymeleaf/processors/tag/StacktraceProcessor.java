package gate.thymeleaf.processors.tag;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.thymeleaf.ELExpression;
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

		JsonArray array = new JsonArray();
		for (Throwable error = exception; error != null; error = error.getCause())
		{
			JsonObject object = new JsonObject();
			object.setString("message", error.getMessage());
			object.set("stacktrace", Stream.of(error.getStackTrace())
				.map(StackTraceElement::toString)
				.map(JsonString::of)
				.collect(Collectors.toCollection(JsonArray::new)));
			array.add(object);
		}

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<script type='module'>");
		string.add("import GStacktrace from './gate/script/g-stacktrace.mjs';");
		string.add("GStacktrace.show('Erro de sistema', " + array.toString() + ");");
		string.add("</script>");

		handler.replaceWith(string.toString(), false);
	}
}
