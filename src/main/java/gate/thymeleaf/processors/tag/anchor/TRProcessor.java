package gate.thymeleaf.processors.tag.anchor;

import gate.Command;
import gate.entity.User;
import gate.io.URL;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import gate.util.Parameters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TRProcessor extends AnchorProcessor
{

	@Inject
	ELExpression expression;

	public TRProcessor()
	{
		super("tr");
	}

	@Override
	protected void process(Model model, Command command, Attributes attributes, Parameters parameters)
	{
		User user = (User) model.session().getAttribute(User.class.getName());

		if (!model.has("condition") || (boolean) expression.evaluate(model.get("condition")))
		{
			if (command.checkAccess(user)
				&& (model.has("module")
				|| model.has("screen")
				|| model.has("action")
				|| model.has("method")
				|| model.has("target")))
			{
				if (model.has("target"))
					attributes.put("data-target", model.get("target"));

				attributes.put("data-action",
					URL.toString(command.getModule(),
						command.getScreen(),
						command.getAction(),
						parameters.toString()));

				if ("POST".equalsIgnoreCase(model.get("method")))
					attributes.put("data-method", "post");
			}

			model.replaceTag("tr", attributes);
		} else
			model.removeAll();
	}

}
