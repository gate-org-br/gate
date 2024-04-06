package gate.thymeleaf.processors.attribute.request;

import gate.Call;
import gate.annotation.Asynchronous;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.AppError;
import gate.error.BadRequestException;
import gate.io.URL;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import gate.util.Parameters;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import java.util.StringJoiner;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebExchange;

public class RequestAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory ELExpressionFactory;

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

		IWebExchange exchange = ((IWebContext) context).getExchange();

		Call call;
		try
		{
			call = Call.of(exchange, module, screen, action);
		} catch (BadRequestException ex)
		{
			throw new AppError(ex);
		}

		User user = CDI.current().select(User.class, Current.LITERAL).get();

		Parameters parameters = new Parameters();

		ELExpression expression = ELExpressionFactory.create();

		Stream.of(element.getAllAttributes())
			.filter(e -> e.getValue() != null)
			.filter(e -> e.getAttributeCompleteName().startsWith("_"))
			.peek(e -> handler.removeAttribute(e.getAttributeCompleteName()))
			.forEach(e -> parameters.put(e.getAttributeCompleteName().substring(1),
			expression.evaluate(e.getValue())));

		if (call.checkAccess(user))
		{
			if (!element.hasAttribute("style"))
				call.getColor().ifPresent(e -> handler.setAttribute("style", "color: " + e));

			if (!element.hasAttribute("data-alert"))
				call.getAlert().ifPresent(e -> handler.setAttribute("data-alert", e));

			if (!element.hasAttribute("data-confirm"))
				call.getConfirm().ifPresent(e -> handler.setAttribute("data-confirm", e));

			if (!element.hasAttribute("data-tooltip"))
				call.getTooltip().ifPresent(e -> handler.setAttribute("data-tooltip", e));

			if (!element.hasAttribute("title"))
			{
				call.getName().ifPresent(e -> handler.setAttribute("title", e));
				call.getDescription().ifPresent(e -> handler.setAttribute("title", e));
			}

			switch (element.getElementCompleteName().toLowerCase())
			{
				case "a":
					handler.setAttribute("href", URL.toString(call.getModule(), call.getScreen(), call.getAction(), parameters.toString()));

					if (call.getMethod().isAnnotationPresent(Asynchronous.class))
						handler.setAttribute("target",
							element.hasAttribute("target")
							? "@progress(" + element.getAttributeValue("target") + ")"
							: "@progress");

					break;
				case "button":
					handler.setAttribute("formaction", URL.toString(call.getModule(), call.getScreen(), call.getAction(), parameters.toString()));

					if (call.getMethod().isAnnotationPresent(Asynchronous.class))
						handler.setAttribute("formtarget",
							element.hasAttribute("formtarget")
							? "@progress(" + element.getAttributeValue("formtarget") + ")"
							: "@progress");
					break;

				case "form":
					handler.setAttribute("action", URL.toString(call.getModule(), call.getScreen(), call.getAction(), parameters.toString()));

					if (call.getMethod().isAnnotationPresent(Asynchronous.class))
						handler.setAttribute("target",
							element.hasAttribute("target")
							? "@progress(" + element.getAttributeValue("target") + ")"
							: "@progress");
					break;
				case "img":
					handler.setAttribute("src", URL.toString(call.getModule(), call.getScreen(), call.getAction(), parameters.toString()));
					break;
				default:
					handler.setAttribute("data-action", URL.toString(call.getModule(), call.getScreen(), call.getAction(), parameters.toString()));

					if (call.getMethod().isAnnotationPresent(Asynchronous.class))
						handler.setAttribute("data-target",
							element.hasAttribute("data-target")
							? "@progress(" + element.getAttributeValue("data-target") + ")"
							: "@progress");
					break;
			}

			if (element instanceof IStandaloneElementTag
				&& (element.getElementCompleteName().toLowerCase().equals("a")
				|| element.getElementCompleteName().toLowerCase().equals("button")))
			{
				StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
				call.getName().ifPresent(body::add);
				call.getIcon().map(e -> "<g-icon>" + e + "</g-icon>")
					.or(() -> call.getEmoji().map(e -> "<e>" + e + "</e>"))
					.ifPresent(body::add);
				handler.setBody(body.toString(), true);
			}
		} else if (element.getElementCompleteName().toLowerCase().equals("a")
			|| element.getElementCompleteName().toLowerCase().equals("button"))
			handler.removeElement();
	}

}
