package gate.thymeleaf.processors.tag;

import gate.Command;
import gate.entity.User;
import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecureProcessor extends ModelProcessor
{

	public SecureProcessor()
	{
		super("secure");
	}

	@Override
	protected void doProcess(Model model)
	{
		User user = (User) model.session().getAttribute(User.class.getName());

		if (Command.of(model.request(),
			model.get("module"),
			model.get("screen"),
			model.get("action"))
			.checkAccess(user))
			model.removeTag();
		else
			model.removeAll();
	}

}
