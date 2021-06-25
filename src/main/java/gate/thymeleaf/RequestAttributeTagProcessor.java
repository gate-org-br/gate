package gate.thymeleaf;

import gate.Command;
import gate.annotation.Current;
import gate.entity.User;
import gate.io.URL;
import gate.util.Parameters;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;

import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

public class RequestAttributeTagProcessor extends AbstractAttributeTagProcessor
{

	public RequestAttributeTagProcessor(String name)
	{
		super(TemplateMode.HTML,
			"g",
			null,
			false,
			name,
			true,
			PRECEDENCE,
			true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag element,
		AttributeName key, String val, IElementTagStructureHandler handler)
	{
		String module = element.getAttributeValue("g:module");
		String screen = element.getAttributeValue("g:screen");
		String action = element.getAttributeValue("g:action");

		handler.removeAttribute("g:module");
		handler.removeAttribute("g:screen");
		handler.removeAttribute("g:action");

		HttpServletRequest request = CDI.current().select(HttpServletRequest.class).get();

		Command command = Command.of(request, module, screen, action);

		User user = CDI.current().select(User.class, Current.LITERAL).get();

		Parameters parameters = new Parameters();
		IEngineConfiguration configuration = context.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

		Stream.of(element.getAllAttributes())
			.filter(e -> e.getValue() != null)
			.filter(e -> e.getAttributeCompleteName().startsWith("_"))
			.peek(e -> handler.removeAttribute(e.getAttributeCompleteName()))
			.forEach(e -> parameters.put(e.getAttributeCompleteName().substring(1),
			parser.parseExpression(context, e.getValue()).execute(context)));

		if (command.checkAccess(user))
		{
			if (element.getElementCompleteName().toLowerCase().equals("a"))
				handler.setAttribute("href", URL.toString(module, screen, action, parameters.toString()));
			else if (element.getElementCompleteName().toLowerCase().equals("button"))
				handler.setAttribute("formaction", URL.toString(module, screen, action, parameters.toString()));
			else
				handler.setAttribute("data-action", URL.toString(module, screen, action, parameters.toString()));

			if (element instanceof IStandaloneElementTag
				&& (element.getElementCompleteName().toLowerCase().equals("a")
				|| element.getElementCompleteName().toLowerCase().equals("button")))
			{
				StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
				command.getName().ifPresent(body::add);
				command.getIcon().map(e -> "<i>" + e + "</i>").ifPresent(body::add);
				handler.setBody(body.toString(), true);
			}
		} else if (element.getElementCompleteName().toLowerCase().equals("a")
			|| element.getElementCompleteName().toLowerCase().equals("button"))
			handler.removeElement();
	}
}
