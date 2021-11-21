package gate.thymeleaf.processors.tag.anchor;

import gate.Command;
import gate.annotation.Asynchronous;
import gate.entity.User;
import gate.io.URL;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import gate.util.Parameters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
	protected void process(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		User user = (User) model.session().getAttribute(User.class.getName());

		if (command.checkAccess(user)
			&& (!model.has("condition")
			|| (boolean) expression.evaluate(model.get("condition"))))
			if ("POST".equalsIgnoreCase(model.get("method")))
				createButton(model, command, attributes, parameters);
			else
				createLink(model, command, attributes, parameters);
		else
			model.removeAll();
	}

	public void createLink(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		attributes.put("href", URL.toString(command.getModule(),
			command.getScreen(),
			command.getAction(),
			parameters.toString()));

		if (model.has("target"))
			if (command.getMethod().isAnnotationPresent(Asynchronous.class))
				if ("_dialog".equals(model.get("target")))
					attributes.put("target", model.get("_progress-dialog"));
				else
					attributes.put("target", model.get("_progress-window"));
			else
				attributes.put("target", model.get("target"));

		if (model.isStandalone())
			model.replaceAll("<a " + attributes + ">" + command.getIcon().map(e -> "<i>" + e + "</i>").orElse("?") + "</a>");
		else
			model.replaceTag("a", attributes);
	}

	public void createButton(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		attributes.put("formaction", URL.toString(command.getModule(),
			command.getScreen(),
			command.getAction(),
			parameters.toString()));

		if (model.has("target"))
			if (command.getMethod().isAnnotationPresent(Asynchronous.class))
				if ("_dialog".equals(model.get("target")))
					attributes.put("formtarget", model.get("_progress-dialog"));
				else
					attributes.put("formtarget", model.get("_progress-window"));
			else
				attributes.put("formtarget", model.get("target"));

		if (model.isStandalone())
			model.replaceAll("<button " + attributes + ">" + command.getIcon().map(e -> "<i>" + e + "</i>").orElse("?") + "</button>");
		else
			model.replaceTag("button", attributes);
	}
}
