package gate.thymeleaf.processors.attribute.request;

import gate.Call;
import gate.Calls;
import gate.annotation.Current;
import gate.entity.User;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import gate.type.RequestCommand;
import gate.util.Parameters;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebExchange;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

public abstract class RequestAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expressionFactory;

	@Inject
	Calls actionRegistry;

	public RequestAttributeProcessor(String name)
	{
		super(null, name);
	}

	@Override
	public void process(ITemplateContext context,
	                    IProcessableElementTag element,
	                    IElementTagStructureHandler handler)
	{
		String module = element.getAttributeValue("g:module");
		String screen = element.getAttributeValue("g:screen");
		String action = element.getAttributeValue("g:action");

		handler.removeAttribute("g:module");
		handler.removeAttribute("g:screen");
		handler.removeAttribute("g:action");

		IWebExchange exchange = ((IWebContext) context).getExchange();
		var request = exchange.getRequest();

		RequestCommand command
				= new RequestCommand(module, screen, action)
				.with(new RequestCommand(request.getParameterValue("MODULE"),
						request.getParameterValue("SCREEN"),
						request.getParameterValue("ACTION"))
						.or(RequestCommand
								.ofPath(Optional.ofNullable(request.getPathWithinApplication())
										.filter(e -> e.startsWith("/Gate/"))
										.map(e -> e.substring("/Gate".length()))
										.orElse(null))));

		Call call = actionRegistry.get(command)
				.orElseThrow(() -> new IllegalArgumentException("Invalid command: " + command));

		User user = CDI.current()
				.select(User.class, Current.LITERAL)
				.get();

		Parameters parameters = new Parameters();
		ELExpression expression = expressionFactory.create();

		Stream.of(element.getAllAttributes())
				.filter(e -> e.getValue() != null)
				.filter(e -> e.getAttributeCompleteName().startsWith("_"))
				.peek(e -> handler.removeAttribute(e.getAttributeCompleteName()))
				.forEach(e
						-> parameters.put(e.getAttributeCompleteName().substring(1),
						expression.evaluate(e.getValue())));

		if (!call.accessRule().allows(user))
		{
			if (element.getElementCompleteName().equalsIgnoreCase("a")
			    || element.getElementCompleteName().equalsIgnoreCase("button"))
				handler.removeElement();
			return;
		}

		var meta = call.metadata();

		if (!element.hasAttribute("style") && meta.color() != null)
			handler.setAttribute("style", "color: " + meta.color());

		if (!element.hasAttribute("data-alert") && meta.alert() != null)
			handler.setAttribute("data-alert", meta.alert());

		if (!element.hasAttribute("data-confirm") && meta.confirm() != null)
			handler.setAttribute("data-confirm", meta.confirm());

		if (!element.hasAttribute("data-tooltip") && meta.tooltip() != null)
			handler.setAttribute("data-tooltip", meta.tooltip());

		if (!element.hasAttribute("title"))
		{
			if (meta.description() != null)
				handler.setAttribute("title", meta.description());
			else if (meta.name() != null)
				handler.setAttribute("title", meta.name());
		}

		String url = call.command().toString(parameters);

		switch (element.getElementCompleteName().toLowerCase())
		{
			case "a" -> handler.setAttribute("href", url);

			case "button" -> handler.setAttribute("formaction", url);

			case "form" -> handler.setAttribute("action", url);

			case "img" -> handler.setAttribute("src", url);

			default -> handler.setAttribute("data-action", url);
		}

		if (call.asynchronous())
		{
			String target
					= element.hasAttribute("target")
					? element.getAttributeValue("target")
					: null;

			String resolved
					= target != null && !target.startsWith("@progress")
					? "@progress > " + target
					: "@progress";

			switch (element.getElementCompleteName().toLowerCase())
			{
				case "a", "form" -> handler.setAttribute("target", resolved);

				case "button" -> handler.setAttribute("formtarget", resolved);

				default -> handler.setAttribute("data-target", resolved);
			}
		}

		if (element instanceof IStandaloneElementTag
		    && (element.getElementCompleteName().equalsIgnoreCase("a")
		        || element.getElementCompleteName().equalsIgnoreCase("button")))
		{
			StringJoiner body = new StringJoiner("").setEmptyValue("unnamed");

			if (meta.name() != null)
				body.add(meta.name());

			if (meta.icon() != null)
				body.add("<g-icon>" + meta.icon() + "</g-icon>");
			else if (meta.emoji() != null)
				body.add("<e>" + meta.emoji() + "</e>");

			handler.setBody(body.toString(), true);
		}
	}
}