package gate.thymeleaf.processors.tag.anchor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import gate.Call;
import gate.entity.User;
import gate.io.URL;
import gate.type.Attributes;
import gate.util.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TRProcessor extends AnchorProcessor
{

	public TRProcessor()
	{
		super("tr");
	}

	@Override
	protected void process(
		ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		User user,
		Call call,
		Attributes attributes,
		Parameters parameters)
	{
		if (!condition(attributes))
		{
			model.reset();
			return;
		}

		boolean hasCommand
			= element.hasAttribute("module")
			|| element.hasAttribute("screen")
			|| element.hasAttribute("action")
			|| element.hasAttribute("method")
			|| element.hasAttribute("target");

		if (hasCommand && call.accessRule().allows(user))
		{
			target(call, attributes)
				.ifPresent(t -> attributes.put("data-target", t));

			attributes.put(
				"data-action",
				URL.toString(call.command(), parameters.toString()));

			if ("POST".equalsIgnoreCase(method(attributes)))
				attributes.put("data-method", "post");
		}

		replaceTag(context, model, handler, "tr", attributes);
	}
}
