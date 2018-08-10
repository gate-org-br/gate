package gateconsole.dao;

import gate.base.Dao;
import gate.entity.Role;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.insert.Insert;
import gate.type.ID;
import java.util.Collection;
import java.util.List;

public class RoleDao extends Dao
{

	public RoleDao()
	{
		super("Gate");
	}

	public RoleDao(Link c)
	{
		super(c);
	}

	public List<Role> search()
	{
		List<Role> roles = getLink()
				.search(Role.class)
				.properties("id", "active", "master", "role.id", "roleID", "+name", "email", "description", "manager.id",
						"manager.name")
				.parameters();

		for (Role p : roles)
			for (Role c : roles)
				if (p.equals(c.getRole()))
					p.getRoles().add(c.setRole(p));
		return roles;
	}

	public Role select(ID id) throws NotFoundException
	{
		return getLink()
				.select(Role.class)
				.properties("=id", "active", "master", "role.id", "roleID", "role.name",
						"email", "name", "description", "manager.id", "manager.name")
				.parameters(id).orElseThrow(NotFoundException::new);
	}

	public void insert(Role value) throws AppException
	{
		getLink().prepare(Insert
				.into("gate.Role")
				.set("active", value.getActive())
				.set("master", value.getMaster())
				.set("Role$id", value.getRole().getId())
				.set("roleID", value.getRoleID())
				.set("name", value.getName())
				.set("email", value.getEmail())
				.set("description", value.getDescription())
				.set("Manager$id", value.getManager().getId())).fetchGeneratedKeys(ID.class)
				.forEach(value::setId);
	}

	public boolean update(Role value) throws AppException
	{
		return getLink()
				.update(Role.class)
				.properties("=id", "active", "master", "role.id", "roleID", "name", "email", "description", "manager.id").execute(value) > 0;
	}

	public boolean delete(Role... values) throws AppException
	{
		return getLink().delete(Role.class).execute(values) > 0;
	}

	public Collection<Role> getChildRoles(Role role)
	{
		return getLink()
				.search(Role.class)
				.properties("=role.id", "id", "active", "master",
						"role.id", "roleID", "role.name", "email", "name",
						"description", "manager.id", "manager.name")
				.parameters(role.getId());
	}
}
