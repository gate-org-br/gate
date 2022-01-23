package gate.thymeleaf.processors.tag.anchor;

import gate.Call;
import gate.entity.User;
import gate.io.URL;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Parameters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class ShortcutProcessor extends AnchorProcessor
{

	@Inject
	ELExpression expression;

	public ShortcutProcessor()
	{
		super("shortcut");
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
		if (call.checkAccess(user) && condition(attributes))
			if ("POST".equalsIgnoreCase(method(attributes)))
				button(context, model, handler, element, call, attributes, parameters);
			else
				link(context, model, handler, element, call, attributes, parameters);
		else
			model.reset();
	}

	public void button(ITemplateContext context,
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
			replaceWith(context, model, handler, "<button " + attributes + ">" + getIcon(call) + "</button>");
		else
			replaceTag(context, model, handler, "button", attributes);
	}

	public void link(ITemplateContext context,
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
			replaceWith(context, model, handler, "<a " + attributes + ">" + getIcon(call) + "</a>");
		else
			replaceTag(context, model, handler, "a", attributes);
	}

	private String getIcon(Call call)
	{
		return call.getIcon().map(e -> "<i>" + e + "</i>")
			.or(() -> call.getEmoji().map(e -> "<e>" + e + "</e>"))
			.orElse("?");
	}
}
