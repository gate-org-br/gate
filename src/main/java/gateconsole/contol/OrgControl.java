package gateconsole.contol;

import gate.base.Control;
import gate.constraint.Constraints;
import gate.entity.Org;
import gate.error.AppException;
import gateconsole.dao.OrgDao;
import java.util.Optional;
import javax.enterprise.context.Dependent;

@Dependent
public class OrgControl extends Control
{

	public Optional<Org> select()
	{
		try (OrgDao dao = new OrgDao())
		{
			return dao.select();
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

	public Object dwload()
	{
		try (OrgDao dao = new OrgDao())
		{
			return dao.dwload();
		}
	}
}
