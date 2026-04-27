package gate.catalog;

import gate.annotation.Current;
import gate.annotation.LinkResource;
import gate.entity.Org;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.sql.select.Select;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Optional;

@ApplicationScoped
public class OrgCatalog
{
	private static final Org DEFAULT = new Org().setOrgID("ORG")
			.setName("Organização");

	@Inject
	@LinkResource("Gate")
	LinkSource linkSource;

	@Current
	@Produces
	@Named("organization")
	@RequestScoped
	public Org select()
	{
		return find().orElse(DEFAULT);
	}

	public Optional<Org> find()
	{
		try (Link link = linkSource.getLink();
		     OrgDao dao = new OrgDao(link))
		{
			return dao.select();
		}
	}

	static class OrgDao extends gate.base.Dao
	{

		public OrgDao(Link link)
		{
			super(link);
		}

		public Optional<Org> select()
		{
			return Select.expression("orgID")
					.expression("name")
					.expression("description")
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
					.connect(getLink())
					.fetchEntity(Org.class);
		}
	}
}