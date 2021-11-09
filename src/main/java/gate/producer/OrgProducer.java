package gate.producer;

import gate.annotation.Current;
import gate.entity.Org;
import gate.sql.Link;
import gate.sql.select.Select;
import java.io.Serializable;
import java.util.Optional;
import javax.enterprise.context.Dependent;
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

	@Inject
	private Control control;

	private static final long serialVersionUID = 1L;
	private static final Org DEFAULT = new Org().setOrgID("ORG")
		.setName("Organização");

	@Current
	@Produces
	@Named("org")
	@RequestScoped
	public Org produce()
	{
		return control.select().orElse(DEFAULT);
	}

	@Dependent
	public static class Control extends gate.base.Control
	{

		public Optional<Org> select()
		{
			try ( Dao dao = new Dao())
			{
				return dao.select();
			}
		}

		public static class Dao extends gate.base.Dao
		{

			public Optional<Org> select()
			{
				try ( Link link = new Link("Gate"))
				{
					return Select.expression("orgID")
						.expression("name")
						.expression("description")
						.expression("authenticators")
						.expression("sun__min")
						.expression("sun__max")
						.expression("mon__min")
						.expression("mon__max")
						.expression("tue__min")
						.expression("tue__max")
						.expression("wed__min")
						.expression("wed__max")
						.expression("thu__min")
						.expression("thu__max")
						.expression("fri__min")
						.expression("fri__max")
						.expression("sat__min")
						.expression("sat__max")
						.from("Org")
						.build()
						.connect(link)
						.fetchEntity(Org.class);
				}
			}
		}
	}
}
