package gate.catalog;

import gate.entity.Role;
import gate.sql.Link;
import gate.type.ID;

import java.util.ArrayList;
import java.util.List;

public class RoleCatalog
{
	public static List<Role> search()
	{
		try (Link link = Link.of("Gate");
		     RoleDao dao = new RoleDao(link))
		{
			return dao.search();
		}
	}

	static class RoleDao extends gate.base.Dao
	{

		public RoleDao(Link link)
		{
			super(link);
		}


		public List<Role> search()
		{
			return getLink()
					.from(getClass().getResource("/gate/catalog/RoleCatalog/search().sql"))
					.constant()
					.fetch(cursor ->
					{
						List<Role> roles = new ArrayList<>();
						if (cursor.next())
						{
							do
							{
								Role role = new Role();
								role.setId(cursor.getValue(ID.class, "id"));
								role.setActive(cursor.getValue(Boolean.class, "active"));
								role.setMaster(cursor.getValue(Boolean.class, "master"));
								role.setRolename(cursor.getValue(String.class, "rolename"));
								role.setName(cursor.getValue(String.class, "name"));
								role.setEmail(cursor.getValue(String.class, "email"));
								role.getRole().setId(cursor.getValue(ID.class, "role.id"));
								role.getManager().setId(cursor.getValue(ID.class, "manager.id"));
								role.getManager().setName(cursor.getValue(String.class, "manager.name"));

								while (cursor.next() && cursor.getValue(ID.class, "id").equals(role.getId()))
									role.getAuths().add(new gate.entity.Auth()
											.setId(cursor.getValue(ID.class, "auth.id"))
											.setScope(cursor.getValue(gate.entity.Auth.Scope.class, "auth.scope"))
											.setAccess(cursor.getValue(gate.entity.Auth.Access.class, "auth.access"))
											.setModule(cursor.getValue(String.class, "auth.module"))
											.setScreen(cursor.getValue(String.class, "auth.screen"))
											.setAction(cursor.getValue(String.class, "auth.action")));

								roles.add(role);
							} while (!cursor.isAfterLast());
						}
						return roles;
					});
		}
	}
}