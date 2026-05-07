package gate.thymeleaf.processors.tag;

import gate.Call;
import gate.annotation.Current;
import gate.converter.Converter;
import gate.entity.User;
import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SecureProcessor extends TagProcessor
{

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
		if (Call.of(((IWebContext) context).getExchange(),
						element.getAttributeValue("module"),
						element.getAttributeValue("screen"),
						element.getAttributeValue("action"))
					.checkAccess(userInstance.get()))
			handler.removeTags();
		else if (element.hasAttribute("otherwise"))
		{
			String otherwise = element.getAttributeValue("otherwise");
			otherwise = Converter.toText(expression.create().evaluate(otherwise));
			handler.replaceWith(otherwise, false);
		} else
			handler.removeElement();
	}
}
