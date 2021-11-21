package gate.thymeleaf.processors.tag;

import gate.thymeleaf.*;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.ITemplateEvent;

@ApplicationScoped
public class ChooseProcessor extends ModelProcessor
{

	@Inject
	ELExpression expression;

	public ChooseProcessor()
	{
		super("choose");
	}

	@Override
	protected void doProcess(Model model)
	{
		IModel otherwise = null;
		var whens = new ArrayList<IModel>();

		int i = 0;
		while (i < model.getModel().size())
		{
			if (isOpenTag(model.getModel().get(i), "g:when"))
			{
				IModel when = model.getContext().getModelFactory().createModel();

				int stack = 0;
				do
				{
					var event = model.getModel().get(i++);

					when.add(event);
					if (isOpenTag(event, "g:when"))
						stack++;
					else if (isCloseTag(event, "g:when"))
						stack--;

				} while (i < model.getModel().size() && stack > 0);

				if (stack != 0)
					throw new TemplateProcessingException("Unbalanced g:when tag");

				whens.add(when);
			} else if (isOpenTag(model.getModel().get(i), "g:otherwise"))
			{
				otherwise = model.getContext().getModelFactory().createModel();

				int stack = 0;
				do
				{
					var event = model.getModel().get(i++);

					otherwise.add(event);
					if (isOpenTag(event, "g:otherwise"))
						stack++;
					else if (isCloseTag(event, "g:otherwise"))
						stack--;

				} while (i < model.getModel().size() && stack > 0);

				if (stack != 0)
					throw new TemplateProcessingException("Unbalanced g:otherwise tag");

			} else
				i++;
		}

		model.removeAll();

		for (IModel imodel : whens)
		{
			IOpenElementTag when = (IOpenElementTag) imodel.get(0);
			if ((boolean) expression.evaluate(when.getAttributeValue("condition")))
			{
				for (int j = 1; j < imodel.size() - 1; j++)
					model.getModel().add(imodel.get(j));
				return;
			}
		}

		if (otherwise != null)
			for (int j = 1; j < otherwise.size() - 1; j++)
				model.getModel().add(otherwise.get(j));

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
