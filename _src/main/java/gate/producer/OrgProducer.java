package gate.producer;

import gate.annotation.Current;
import gate.entity.Org;
import gate.error.AppException;
import gateconsole.contol.OrgControl;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author davins
 *
 * Produces an Org object with current organization data.
 *
 */
@ApplicationScoped
public class OrgProducer implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Org org;

	@Inject
	private OrgControl control;

	@Current
	@Produces
	@Named(value = "org")
	public Org produce()
	{
		try
		{
			if (org == null)
				org = control.select();
			return org;
		} catch (AppException ex)
		{
			return new Org()
					.setOrgID("ORG")
					.setName("Organização");
		}
	}

}
