package gate.thymeleaf.processors.attribute;

import gate.Call;
import gate.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.error.BadRequestException;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebExchange;

@ApplicationScoped
public class SecureAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public SecureAttributeProcessor()
	{
		super(null, "secure");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		try
		{
			String[] path = element.getAttributeValue("g:secure").split("/");
			String module = path.length >= 1 ? path[0] : null;
			String screen = path.length >= 2 ? path[1] : null;
			String action = path.length >= 3 ? path[2] : null;

			IWebExchange exchange = ((IWebContext) context).getExchange();
			User user = (User) exchange.getSession().getAttributeValue(User.class.getName());

			if (Call.of(module, screen, action).checkAccess(user))
			{
				handler.removeAttribute("g:secure");
			} else if (element.hasAttribute("g:otherwise"))
			{
				String otherwise = element.getAttributeValue("g:otherwise");
				otherwise = Converter.toText(expression.create().evaluate(otherwise));
				handler.replaceWith(otherwise, false);
			} else
				handler.removeElement();
		} catch (BadRequestException ex)
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
