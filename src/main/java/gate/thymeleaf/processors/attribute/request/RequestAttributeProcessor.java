package gate.thymeleaf.processors.attribute.request;

import gate.Command;
import gate.annotation.Asynchronous;
import gate.annotation.Current;
import gate.entity.User;
import gate.io.URL;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import gate.util.Parameters;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

public class RequestAttributeProcessor extends AttributeProcessor
{

	public RequestAttributeProcessor(String name)
	{
		super(null, name);
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{

		String module = element.getAttributeValue("g:module");
		String screen = element.getAttributeValue("g:screen");
		String action = element.getAttributeValue("g:action");

		handler.removeAttribute("g:module");
		handler.removeAttribute("g:screen");
		handler.removeAttribute("g:action");

		HttpServletRequest request = ((IWebContext) context).getRequest();

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
			if (!element.hasAttribute("style"))
				command.getColor().ifPresent(e -> handler.setAttribute("style", "color: " + e));

			if (!element.hasAttribute("data-alert"))
				command.getAlert().ifPresent(e -> handler.setAttribute("data-alert", e));

			if (!element.hasAttribute("data-confirm"))
				command.getConfirm().ifPresent(e -> handler.setAttribute("data-confirm", e));

			if (!element.hasAttribute("data-tooltip"))
				command.getTooltip().ifPresent(e -> handler.setAttribute("data-tooltip", e));

			if (!element.hasAttribute("title"))
				command.getDescription().ifPresent(e -> handler.setAttribute("description", e));

			if (!element.hasAttribute("title"))
				command.getName().ifPresent(e -> handler.setAttribute("title", e));

			switch (element.getElementCompleteName().toLowerCase())
			{
				case "a":
					handler.setAttribute("href", URL.toString(command.getModule(), command.getScreen(), command.getAction(), parameters.toString()));

					if (command.getMethod().isAnnotationPresent(Asynchronous.class))
						if ("_dialog".equals(element.getAttributeValue("target")))
							handler.setAttribute("target", "_progress-dialog");
						else
							handler.setAttribute("target", "_progress-window");

					break;
				case "button":
					handler.setAttribute("formaction", URL.toString(command.getModule(), command.getScreen(), command.getAction(), parameters.toString()));

					if (command.getMethod().isAnnotationPresent(Asynchronous.class))
						if ("_dialog".equals(element.getAttributeValue("formtarget")))
							handler.setAttribute("formtarget", "_progress-dialog");
						else
							handler.setAttribute("formtarget", "_progress-window");

					break;

				case "form":
					handler.setAttribute("action", URL.toString(command.getModule(), command.getScreen(), command.getAction(), parameters.toString()));

					if (command.getMethod().isAnnotationPresent(Asynchronous.class))
						if ("_dialog".equals(element.getAttributeValue("target")))
							handler.setAttribute("target", "_progress-dialog");
						else
							handler.setAttribute("target", "_progress-window");

					break;
				default:
					handler.setAttribute("data-action", URL.toString(command.getModule(), command.getScreen(), command.getAction(), parameters.toString()));

					if (command.getMethod().isAnnotationPresent(Asynchronous.class))
						if ("_dialog".equals(element.getAttributeValue("formtarget")))
							handler.setAttribute("data-target", "_progress-dialog");
						else
							handler.setAttribute("data-target", "_progress-window");
					break;
			}

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
