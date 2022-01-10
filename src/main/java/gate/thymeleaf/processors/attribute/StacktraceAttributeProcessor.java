package gate.thymeleaf.processors.attribute;

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

				JsonArray array = new JsonArray();
				for (Throwable error = throwable; error != null; error = error.getCause())
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
				string.add("");
				string.add("import GStacktrace from './gate/script/g-stacktrace.mjs';");
				string.add("GStacktrace.show('Erro de sistema', " + array.toString() + ");");

				handler.setBody(string.toString(), false);
			}, handler::removeElement);
	}
}
