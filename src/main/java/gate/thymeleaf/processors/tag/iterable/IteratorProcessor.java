package gate.thymeleaf.processors.tag.iterable;

import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.model.IModel;

@ApplicationScoped
public class IteratorProcessor extends IterableProcessor
{

	public IteratorProcessor()
	{
		super("iterator");
	}

	@Override
	protected void process(Model model)
	{
		model.removeTag();
		IModel content = model.cloneModel();

		model.removeAll();
		iterate(model, content);
	}
}
