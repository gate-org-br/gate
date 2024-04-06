package gate.thymeleaf.processors.tag.anchor;

import gate.Call;
import gate.annotation.Asynchronous;
import gate.entity.User;
import gate.error.AppError;
import gate.error.BadRequestException;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.processors.tag.TagModelProcessor;
import gate.type.Attributes;
import gate.util.Parameters;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

public abstract class AnchorProcessor extends TagModelProcessor
{

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

		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		Parameters parameters = new Parameters();
		if (attributes.containsKey("arguments"))
			Parameters.parse((String) attributes.remove("arguments")).entrySet()
				.forEach(entry -> parameters.put(entry.getKey(),
				expression.create().evaluate(entry.getValue().toString())));

		attributes.entrySet().stream()
			.filter(e -> e.getValue() != null)
			.filter(e -> e.getKey().startsWith("_"))
			.forEach(e -> parameters.put(e.getKey().substring(1), expression.create().evaluate((String) e.getValue())));
		attributes.entrySet().removeIf(e -> e.getKey().startsWith("_"));

		var exchange = ((IWebContext) context).getExchange();

		Call call;
		try
		{
			call = Call.of(exchange,
				(String) attributes.remove("module"),
				(String) attributes.remove("screen"),
				(String) attributes.remove("action"));
		} catch (BadRequestException ex)
		{
			throw new AppError(ex);
		}

		if (!attributes.containsKey("style"))
			call.getColor().ifPresent(e -> attributes.put("style", "color: " + e));

		if (!attributes.containsKey("data-tooltip"))
			call.getTooltip().ifPresent(e -> attributes.put("data-tooltip", e));

		if (!attributes.containsKey("data-confirm"))
			call.getConfirm().ifPresent(e -> attributes.put("data-confirm", e));

		if (!attributes.containsKey("data-alert"))
			call.getAlert().ifPresent(e -> attributes.put("data-alert", e));

		if (!attributes.containsKey("title"))
		{
			call.getDescription().ifPresent(e -> attributes.put("title", e));
			call.getName().ifPresent(e -> attributes.put("title", e));
		}

		User user = (User) exchange.getSession().getAttributeValue(User.class.getName());

		process(context, model, handler, element, user, call, attributes, parameters);
	}

	protected boolean condition(Attributes attributes)
	{
		if (!attributes.containsKey("condition"))
			return true;
		String attribute = (String) attributes.remove("condition");
		boolean condition = (boolean) expression.create().evaluate(attribute);
		return condition;
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
			Optional.empty();

		String target = (String) attributes.remove("target");
		target = (String) expression.create().evaluate(target);

		if (call.getMethod().isAnnotationPresent(Asynchronous.class))
			return Optional.of(target != null ? "@progress(" + target + ")" : "@progress");
		else
			return Optional.ofNullable(target);
	}

	protected abstract void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler,
		IProcessableElementTag element,
		User user, Call call, Attributes attributes, Parameters parameters);
}
