package gate.thymeleaf.processors.tag;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import gate.Calls;
import gate.annotation.Current;
import gate.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.error.BadRequestException;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SecureProcessor extends TagProcessor
{

	@Inject
	@Current
	User user;

	@Inject
	Calls actionRegistry;

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

			if (actionRegistry.canAccess(user, command))
			{
				handler.removeTags();
			} else if (element.hasAttribute("otherwise"))
			{
				String otherwise = element.getAttributeValue("otherwise");
				otherwise = Converter.toText(expression.create().evaluate(otherwise));
				handler.replaceWith(otherwise, false);
			} else
				handler.removeElement();

		} catch (BadRequestException ex)
		{
			throw new AppError(ex);
		}
	}
}
