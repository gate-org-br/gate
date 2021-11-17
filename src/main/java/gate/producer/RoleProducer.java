package gate.producer;

import gate.entity.Role;
import gate.error.AppException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.Hierarchy;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

public class RoleProducer
{

	@Inject
	private Control control;

	@Produces
	@RequestScoped
	@Named(value = "roles")
	public List<Role> getRoles()
		throws AppException
	{
		return control.search();
	}

	@Dependent
	public static class Control extends gate.base.Control
	{

		public List<Role> search() throws AppException
		{
			try ( Dao dao = new Dao())
			{
				return Hierarchy.setup(dao.search());
			}
		}

		public static class Dao extends gate.base.Dao
		{

			public List<Role> search()
			{
				try ( Link link = new Link("Gate"))
				{

					return Select.expression("Role.id").as("id")
						.expression("Role.active")
						.expression("Role.master")
						.expression("Role.Role$id").as("role.id")
						.expression("Role.rolename")
						.expression("Role.name")
						.expression("Role.email")
						.expression("Role.description")
						.expression("Manager.id").as("manager.id")
						.expression("Manager.name").as("manager.name")
						.from("Role")
						.leftJoin("Uzer").as("Manager").on(Condition.of("Role.Manager$id").isEq("Manager.id"))
						.orderBy("Role.name")
						.build()
						.connect(link)
						.fetchEntityList(Role.class);
				}
			}
		}
	}
}
