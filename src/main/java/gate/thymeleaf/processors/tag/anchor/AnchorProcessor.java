package gate.thymeleaf.processors.tag.anchor;

import gate.Call;
import gate.Calls;
import gate.annotation.Current;
import gate.entity.User;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.processors.tag.TagModelProcessor;
import gate.type.Attributes;
import gate.type.RequestCommand;
import gate.util.Parameters;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AnchorProcessor extends TagModelProcessor
{

	@Inject
	@Current
	User user;

	@Inject
	Calls actionRegistry;

	@Inject
	ELExpressionFactory expression;

	public AnchorProcessor(String name)
	{
		super(name);
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		Attributes attributes = Stream.of(element.getAllAttributes()).collect(Collectors
				.toMap(IAttribute::getAttributeCompleteName, IAttribute::getValue, (a, b) -> a, Attributes::new));

		Parameters parameters = new Parameters();
		if (attributes.containsKey("arguments"))
			Parameters.parse((String) attributes.remove("arguments"))
					.entrySet()
					.forEach(entry -> parameters.put(entry.getKey(),
							expression.create().evaluate(entry.getValue().toString())));

		attributes.entrySet().stream().filter(e -> e.getValue() != null).filter(e -> e.getKey().startsWith("_"))
				.forEach(e -> parameters.put(e.getKey().substring(1),
						expression.create().evaluate((String) e.getValue())));
		attributes.entrySet().removeIf(e -> e.getKey().startsWith("_"));

		var exchange = ((IWebContext) context).getExchange();
		var request = exchange.getRequest();

		var command = new RequestCommand(
				(String) attributes.remove("module"),
				(String) attributes.remove("screen"),
				(String) attributes.remove("action"))
				.with(new RequestCommand(request.getParameterValue("MODULE"),
						request.getParameterValue("SCREEN"),
						request.getParameterValue("ACTION"))
						.or(RequestCommand
								.ofPath(Optional.ofNullable(request.getPathWithinApplication())
										.filter(e -> e.startsWith("/Gate/"))
										.map(e -> e.substring("/Gate".length()))
										.orElse(null))));

		var call = actionRegistry.get(command)
				.orElseThrow(() -> new IllegalArgumentException("Invalid command: " + command));

		var metadata = call.metadata();
		if (metadata.color() != null)
			attributes.putIfAbsent("style", "color: " + metadata.color());
		if (metadata.tooltip() != null)
			attributes.putIfAbsent("data-tooltip", metadata.tooltip());
		if (metadata.confirm() != null)
			attributes.putIfAbsent("data-confirm", metadata.confirm());
		if (metadata.alert() != null)
			attributes.putIfAbsent("data-alert", metadata.alert());
		if (!attributes.containsKey("title"))
		{
			if (metadata.description() != null)
				attributes.put("title", metadata.description());
			if (metadata.name() != null)
				attributes.put("title", metadata.name());
		}

		process(context, model, handler, element, user, call, attributes, parameters);
	}

	protected boolean condition(Attributes attributes)
	{
		if (!attributes.containsKey("condition"))
			return true;
		String attribute = (String) attributes.remove("condition");
		return (boolean) expression.create().evaluate(attribute);
	}

	protected String method(Attributes attributes)
	{
		if (!attributes.containsKey("method"))
			return "GET";
		String method = (String) attributes.remove("method");
		method = (String) expression.create().evaluate(method);
		return method;
	}

	protected Optional<String> target(Call call, Attributes attributes)
	{
		if (!attributes.containsKey("target"))
			return Optional.empty();

		String target = (String) attributes.remove("target");
		target = (String) expression.create().evaluate(target);

		if (call.asynchronous())
			return Optional
					.of(target != null && !target.startsWith("@progress") ? "@progress > " + target : "@progress");
		else
			return Optional.ofNullable(target);
	}

	protected abstract void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler,
									IProcessableElementTag element, User user, Call call, Attributes attributes, Parameters parameters);
}