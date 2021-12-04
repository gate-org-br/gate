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
import org.thymeleaf.processor.element.IElementModelStructureHandler;

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
	protected void process(ITemplateContext context,
		IModel model,
		IElementModelStructureHandler handler,
		IProcessableElementTag element,
		User user,
		Call call,
		Attributes attributes,
		Parameters parameters)
	{
		if (condition(attributes))
		{
			if (call.checkAccess(user)
				&& (element.hasAttribute("module")
				|| element.hasAttribute("screen")
				|| element.hasAttribute("action")
				|| element.hasAttribute("method")
				|| element.hasAttribute("target")))
			{

				target(call, attributes).ifPresent(target -> attributes.put("data-target", target));
				attributes.put("data-action", URL.toString(call.getModule(), call.getScreen(),
					call.getAction(), parameters.toString()));
				if ("POST".equalsIgnoreCase(method(attributes)))
					attributes.put("data-method", "post");
			}

			replaceTag(context, model, handler, "tr", attributes);
		} else
			model.reset();
	}

}
