package gate.producer;

import gate.annotation.Current;
import gate.entity.Org;
import gateconsole.contol.OrgControl;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
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
public class OrgProducer implements Serializable
{

	private static final long serialVersionUID = 1L;
	private static final Org DEFAULT = new Org().setOrgID("ORG")
		.setName("Organização");

	@Inject
	private OrgControl control;

	@Current
	@Produces
	@Named("org")
	@RequestScoped
	public Org produce()
	{
		return control.select().orElse(DEFAULT);
	}

}
