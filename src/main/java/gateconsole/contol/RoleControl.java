package gateconsole.contol;

import gate.base.Control;
import gate.constraint.Constraints;
import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.type.ID;
import gateconsole.dao.AuthDao;
import gateconsole.dao.RoleDao;
import gateconsole.dao.UserDao;
import java.util.Collection;
import java.util.List;

public class RoleControl extends Control
{

	public Collection<Role> search() throws AppException
	{
		try (Link link = new Link("Gate");
				RoleDao roleDao = new RoleDao(link);
				UserDao userDao = new UserDao(link);
				AuthDao authDao = new AuthDao(link))
		{
			Collection<Role> roles = roleDao.search();
			Collection<User> users = userDao.search();
			Collection<Auth> auths = authDao.search();
			roles.forEach(role -> auths.stream().filter(e -> role.equals(e.getRole())).forEach(role.getAuths()::add));
			roles.forEach(role -> users.stream().filter(e -> role.equals(e.getRole())).forEach(role.getUsers()::add));
			users.forEach(user -> auths.stream().filter(e -> user.equals(e.getUser())).forEach(user.getAuths()::add));
			roles.removeIf(e -> e.getRole().getId() != null);
			return roles;
		}
	}

	public Role select(ID id) throws AppException
	{
		try (RoleDao dao = new RoleDao())
		{
			return dao.select(id);
		}
	}

	public void insert(Role role) throws AppException
	{
		Constraints.validate(role, "master", "active", "name", "email", "description", "roleID");
		try (RoleDao dao = new RoleDao())
		{
			dao.insert(role);
		}
	}

	public void update(Role role) throws AppException
	{
		Constraints.validate(role, "role.id", "master", "active",
				"name", "email", "description", "roleID");

		try (RoleDao dao = new RoleDao())
		{
			dao.beginTran();

			if (!dao.update(role))
				throw new AppException("Tentativa de alterar um perfil inexistente.");

			List<Role> roles = dao.search();
			if (roles.stream().anyMatch(e
					-> Boolean.TRUE.equals(e.getMaster())
					&& e.getParent().getId() != null
					&& !Boolean.TRUE.equals(e.getParent().getMaster())))
				throw new AppException("Tentativa de inserir um perfil master dentro de um perfil não master.");

			dao.commit();
		}
	}

	public void delete(Role role) throws AppException
	{

		try (RoleDao dao = new RoleDao())
		{
			if (!dao.delete(role))
				throw new AppException("Tentativa de remover um perfil inexistente.");
		} catch (ConstraintViolationException e)
		{
			throw new AppException(e.getMessage());
		}
	}

	public Collection<Role> getChildRoles(Role role)
	{
		try (RoleDao dao = new RoleDao())
		{
			return dao.getChildRoles(role);
		}
	}
}
