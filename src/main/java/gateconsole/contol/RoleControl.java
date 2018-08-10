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

public class RoleControl extends Control
{

	public Collection<Role> search()
	{
		try (Link link = new Link("Gate");
				RoleDao roleDao = new RoleDao(link);
				UserDao userDao = new UserDao(link);
				AuthDao authDao = new AuthDao(link))
		{
			Collection<Role> roles = roleDao.search();
			Collection<User> users = userDao.search();
			Collection<Auth> auths = authDao.search();
			roles.forEach(role -> auths.stream().filter(e -> role.getId().equals(e.getRole().getId())).forEach(e -> role.getAuths().add(e)));
			roles.forEach(role -> users.stream().filter(e -> role.getId().equals(e.getRole().getId())).forEach(e -> role.getUsers().add(e)));
			users.forEach(user -> auths.stream().filter(e -> user.getId().equals(e.getUser().getId())).forEach(e -> user.getAuths().add(e)));
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
		Constraints.validate(role, "role.id", "master", "active", "name", "email", "description", "roleID");

		try (RoleDao dao = new RoleDao())
		{
			dao.beginTran();

			if (role.equals(role.getRole())
					|| dao.search().stream().anyMatch(e -> e.isParentOf(role) && e.isChildOf(role)))
				throw new AppException("Tentativa de criar associação cíclica entre perfis");

			if (!dao.update(role))
				throw new AppException("Tentativa de alterar um perfil inexistente.");

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
