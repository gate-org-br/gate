package gate.thymeleaf.processors.tag;

import gate.entity.User;
import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SuperUserProcessor extends ModelProcessor
{

	public SuperUserProcessor()
	{
		super("superuser");
	}

	@Override
	protected void doProcess(Model model)
	{
		User user = (User) model.session().getAttribute(User.class.getName());
		if (user != null && user.isSuperUser())
			model.removeTag();
		else
			model.removeAll();
	}

}
