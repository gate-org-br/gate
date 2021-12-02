package gate.thymeleaf.processors.tag;

import gate.thymeleaf.*;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class ChooseProcessor extends TagModelProcessor
{

	@Inject
	ELExpression expression;

	public ChooseProcessor()
	{
		super("choose");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IModel otherwise = null;
		var whens = new ArrayList<IModel>();

		int i = 0;
		while (i < model.size())
		{
			if (isOpenTag(model.get(i), "g:when"))
			{
				IModel when = context.getModelFactory().createModel();

				int stack = 0;
				do
				{
					var event = model.get(i++);

					when.add(event);
					if (isOpenTag(event, "g:when"))
						stack++;
					else if (isCloseTag(event, "g:when"))
						stack--;

				} while (i < model.size() && stack > 0);

				if (stack != 0)
					throw new TemplateProcessingException("Unbalanced g:when tag");

				whens.add(when);
			} else if (isOpenTag(model.get(i), "g:otherwise"))
			{
				otherwise = context.getModelFactory().createModel();

				int stack = 0;
				do
				{
					var event = model.get(i++);

					otherwise.add(event);
					if (isOpenTag(event, "g:otherwise"))
						stack++;
					else if (isCloseTag(event, "g:otherwise"))
						stack--;

				} while (i < model.size() && stack > 0);

				if (stack != 0)
					throw new TemplateProcessingException("Unbalanced g:otherwise tag");

			} else
				i++;
		}

		model.reset();

		for (IModel imodel : whens)
		{
			IOpenElementTag when = (IOpenElementTag) imodel.get(0);
			if ((boolean) expression.evaluate(when.getAttributeValue("condition")))
			{
				for (int j = 1; j < imodel.size() - 1; j++)
					model.add(imodel.get(j));
				return;
			}
		}

		if (otherwise != null)
			for (int j = 1; j < otherwise.size() - 1; j++)
				model.add(otherwise.get(j));

	}

	private boolean isOpenTag(ITemplateEvent event, String name)
	{
		return event instanceof IOpenElementTag
			&& name.equals(((IOpenElementTag) event).getElementCompleteName());
	}

	private boolean isCloseTag(ITemplateEvent event, String name)
	{
		return event instanceof ICloseElementTag
			&& name.equals(((ICloseElementTag) event).getElementCompleteName());
	}
}
