package gate.thymeleaf.processors.tag.anchor;

import gate.Command;
import gate.annotation.Asynchronous;
import gate.converter.Converter;
import gate.entity.User;
import gate.io.URL;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import gate.util.Parameters;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
	protected void process(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		User user = (User) model.session().getAttribute(User.class.getName());

		if (command.checkAccess(user))
			if (!model.has("condition") || (boolean) expression.evaluate(model.get("condition")))
				if ("POST".equalsIgnoreCase((String) expression.evaluate(model.get("method"))))
					createButton(model, command, attributes, parameters);
				else
					createLink(model, command, attributes, parameters);
			else if (model.has("otherwise"))
				model.replaceAll(Converter.toText(expression.evaluate(model.get("otherwise"))));
			else
				model.removeAll();
		else
			model.removeAll();
	}

	private void createButton(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		attributes.put("formaction", URL.toString(command.getModule(),
			command.getScreen(),
			command.getAction(),
			parameters.toString()));

		if (model.has("target"))
			if (command.getMethod().isAnnotationPresent(Asynchronous.class))
				if ("_dialog".equals(expression.evaluate(model.get("target"))))
					attributes.put("formtarget", expression.evaluate(model.get("_progress-dialog")));
				else
					attributes.put("formtarget", expression.evaluate(model.get("_progress-window")));
			else
				attributes.put("formtarget", expression.evaluate(model.get("target")));

		if (model.isStandalone())
		{
			StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
			command.getName().ifPresent(body::add);
			command.getIcon().map(e -> "<i>" + e + "</i>").ifPresent(body::add);
			model.replaceAll("<button " + attributes + ">" + body + "</button>");
		} else
			model.replaceTag("button", attributes);
	}

	private void createLink(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		attributes.put("href", URL.toString(command.getModule(),
			command.getScreen(),
			command.getAction(),
			parameters.toString()));

		if (model.has("target"))
			if (command.getMethod().isAnnotationPresent(Asynchronous.class))
				if ("_dialog".equals(expression.evaluate(model.get("target"))))
					attributes.put("target", expression.evaluate(model.get("_progress-dialog")));
				else
					attributes.put("target", expression.evaluate(model.get("_progress-window")));
			else
				attributes.put("target", expression.evaluate(model.get("target")));

		if (model.isStandalone())
		{
			StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
			command.getName().ifPresent(body::add);
			command.getIcon().map(e -> "<i>" + e + "</i>").ifPresent(body::add);
			model.replaceAll("<a " + attributes + ">" + body + "</a>");
		} else
			model.replaceTag("a", attributes);
	}
}
