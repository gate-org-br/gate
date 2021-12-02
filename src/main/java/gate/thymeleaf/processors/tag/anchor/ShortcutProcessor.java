package gate.thymeleaf.processors.tag.anchor;

import gate.Command;
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
		Command command,
		Attributes attributes,
		Parameters parameters)
	{
		if (command.checkAccess(user) && condition(attributes))
			if ("POST".equalsIgnoreCase(method(attributes)))
				button(context, model, handler, element, command, attributes, parameters);
			else
				link(context, model, handler, element, command, attributes, parameters);
		else
			model.reset();
	}

	public void button(ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		Command command,
		Attributes attributes,
		Parameters parameters)
	{
		attributes.put("formaction", URL.toString(command.getModule(),
			command.getScreen(),
			command.getAction(),
			parameters.toString()));

		target(command, attributes).ifPresent(target -> attributes.put("formtarget", target));

		if (element instanceof IStandaloneElementTag)
		{
			String icon = command.getIcon().map(e -> "<i>" + e + "</i>").orElse("?");
			replaceWith(context, model, handler, "<button " + attributes + ">" + icon + "</button>");
		} else
			replaceTag(context, model, handler, "button", attributes);
	}

	public void link(ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		Command command,
		Attributes attributes,
		Parameters parameters)
	{
		attributes.put("href", URL.toString(command.getModule(),
			command.getScreen(),
			command.getAction(),
			parameters.toString()));

		target(command, attributes).ifPresent(target -> attributes.put("target", target));

		if (element instanceof IStandaloneElementTag)
		{
			String icon = command.getIcon().map(e -> "<i>" + e + "</i>").orElse("?");
			replaceWith(context, model, handler, "<a " + attributes + ">" + icon + "</button>");
		} else
			replaceTag(context, model, handler, "a", attributes);
	}
}
