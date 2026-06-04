package gate.thymeleaf.processors.tag;

import gate.CallRegistry;
import gate.adapter.renderer.Renderer;
import gate.annotation.Current;
import gate.entity.User;

import gate.error.BadRequestException;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SecureProcessor extends TagProcessor
{

	@Inject
	CallRegistry actionRegistry;

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	ELExpressionFactory expression;

	public SecureProcessor()
	{
		super("secure");
	}

	@Override
	public void process(ITemplateContext context,
	                    IProcessableElementTag element,
	                    IElementTagStructureHandler handler)
	{
		try
		{
			RequestCommand command = new RequestCommand(
					element.getAttributeValue("module"),
					element.getAttributeValue("screen"),
					element.getAttributeValue("action"));

			if (actionRegistry.canAccess(userInstance.get(), command))
			{
				handler.removeTags();
			} else if (element.hasAttribute("otherwise"))
			{
				String otherwise = element.getAttributeValue("otherwise");
				otherwise = Renderer.render(expression.create().evaluate(otherwise));
				handler.replaceWith(otherwise, false);
			} else
				handler.removeElement();

		} catch (BadRequestException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}