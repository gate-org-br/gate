package gate.thymeleaf.processors.tag.anchor;

import gate.Call;
import gate.converter.Converter;
import gate.entity.User;
import gate.io.URL;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Parameters;
import java.util.Optional;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class LinkProcessor extends AnchorProcessor
{

	@Inject
	ELExpression expression;

	public LinkProcessor()
	{
		super("link");
	}

	@Override
	protected void process(ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		User user,
		Call call,
		Attributes attributes,
		Parameters parameters)
	{
		if (call.checkAccess(user))
			if (condition(attributes))
				if ("POST".equalsIgnoreCase(method(attributes)))
					button(context, model, handler, element, call, attributes, parameters);
				else
					link(context, model, handler, element, call, attributes, parameters);
			else
				otherwise(attributes).ifPresentOrElse(e -> replaceWith(context, model, handler, e), model::reset);
		else
			model.reset();
	}

	private void button(ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		Call call,
		Attributes attributes,
		Parameters parameters)
	{
		attributes.put("formaction", URL.toString(call.getModule(),
			call.getScreen(),
			call.getAction(),
			parameters.toString()));

		target(call, attributes).ifPresent(target -> attributes.put("formtarget", target));

		if (element instanceof IStandaloneElementTag)
		{
			StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
			call.getName().ifPresent(body::add);
			getIcon(call).ifPresent(body::add);
			replaceWith(context, model, handler, "<button " + attributes + ">" + body + "</button>");
		} else
			replaceTag(context, model, handler, "button", attributes);
	}

	private void link(ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		Call call,
		Attributes attributes,
		Parameters parameters)
	{
		attributes.put("href", URL.toString(call.getModule(),
			call.getScreen(),
			call.getAction(),
			parameters.toString()));

		target(call, attributes).ifPresent(target -> attributes.put("target", target));

		if (element instanceof IStandaloneElementTag)
		{
			StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
			call.getName().ifPresent(body::add);
			getIcon(call).ifPresent(body::add);
			replaceWith(context, model, handler, "<a " + attributes + ">" + body + "</a>");
		} else
			replaceTag(context, model, handler, "a", attributes);
	}

	private Optional<String> otherwise(Attributes attributes)
	{
		if (!attributes.containsKey("otherwise"))
			return Optional.empty();
		Object otherwise = attributes.remove("otherwise");
		otherwise = expression.evaluate((String) otherwise);
		if (otherwise == null)
			return Optional.empty();
		return Optional.of(Converter.toText(otherwise));
	}

	private Optional<String> getIcon(Call call)
	{
		return call.getIcon().map(e -> "<i>" + e + "</i>")
			.or(() -> call.getEmoji().map(e -> "<e>" + e + "</e>"));
	}
}
