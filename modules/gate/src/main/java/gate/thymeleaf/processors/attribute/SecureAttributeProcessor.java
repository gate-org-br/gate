package gate.thymeleaf.processors.attribute;

import gate.adapter.renderer.Renderer;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import gate.Calls;
import gate.annotation.Current;
import gate.adapter.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SecureAttributeProcessor extends AttributeProcessor
{

	@Inject
	@Current
	User user;

	@Inject
	Calls actionRegistry;

	@Inject
	ELExpressionFactory expression;

	public SecureAttributeProcessor()
	{
		super(null, "secure");
	}

	@Override
	public void process(
			ITemplateContext context,
			IProcessableElementTag element,
			IElementTagStructureHandler handler)
	{
		try
		{
			String[] path = element.getAttributeValue("g:secure").split("/");

			String module = path.length > 0 ? path[0] : null;
			String screen = path.length > 1 ? path[1] : null;
			String action = path.length > 2 ? path[2] : null;

			RequestCommand command
					= new RequestCommand(module, screen, action);

			if (actionRegistry.canAccess(user, command))
			{
				handler.removeAttribute("g:secure");
				return;
			}

			if (element.hasAttribute("g:otherwise"))
			{
				String otherwise = element.getAttributeValue("g:otherwise");
				otherwise = Renderer.render(
						expression.create().evaluate(otherwise));
				handler.replaceWith(otherwise, false);
			} else
			{
				handler.removeElement();
			}

		} catch (RuntimeException ex)
		{
			throw new AppError(ex);
		}
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}
}