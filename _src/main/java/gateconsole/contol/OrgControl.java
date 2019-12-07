package gateconsole.contol;

import gate.base.Control;
import gate.entity.Org;
import gate.error.AppException;
import gate.constraint.Constraints;
import gate.error.NotFoundException;
import gateconsole.dao.OrgDao;
import javax.enterprise.context.Dependent;

@Dependent
public class OrgControl extends Control
{

	public Org select() throws NotFoundException
	{
		try (OrgDao dao = new OrgDao())
		{
			return dao.select().orElseThrow(NotFoundException::new);
		}
	}

	public void update(Org model) throws AppException
	{
		Constraints.validate(model, "orgID", "name", "description", "authenticators", "sun", "mon", "tue", "wed", "fri", "thu", "sat");
		try (OrgDao dao = new OrgDao())
		{
			dao.getLink().beginTran();
			if (!dao.update(model))
				dao.insert(model);

			if (model.getIcon() != null)
				dao.update(model.getIcon());

			dao.getLink().commit();
		}
	}
}
