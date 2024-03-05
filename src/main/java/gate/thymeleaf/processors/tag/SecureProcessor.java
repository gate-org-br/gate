package gate.thymeleaf.processors.tag;

import gate.Call;
import gate.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.error.BadRequestException;
import gate.thymeleaf.ELExpressionFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
			HttpServletRequest request = ((IWebContext) context).getRequest();
			User user = (User) request.getSession().getAttribute(User.class.getName());

			if (Call.of(request,
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
