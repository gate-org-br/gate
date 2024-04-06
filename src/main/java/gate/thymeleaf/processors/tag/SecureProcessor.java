package gate.thymeleaf.processors.tag;

import gate.Call;
import gate.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.error.BadRequestException;
import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SecureProcessor extends TagProcessor
{

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
			var exchange = ((IWebContext) context).getExchange();
			User user = (User) exchange.getSession().getAttributeValue(User.class.getName());

			if (Call.of(exchange,
				element.getAttributeValue("module"),
				element.getAttributeValue("screen"),
				element.getAttributeValue("action"))
				.checkAccess(user))
				handler.removeTags();
			else if (element.hasAttribute("otherwise"))
			{
				String otherwise = element.getAttributeValue("otherwise");
				otherwise = Converter.toText(expression.create().evaluate(otherwise));
				handler.replaceWith(otherwise, false);
			} else
				handler.removeTags();
		} catch (BadRequestException ex)
		{
			throw new AppError(ex);
		}
	}
}
