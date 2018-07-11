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
import java.util.Iterator;

public class RoleControl extends Control
{

	public Collection<Role> search()
	{
		try (Link c = new Link("Gate");
				RoleDao roleDao = new RoleDao(c);
				UserDao userDao = new UserDao(c);
				AuthDao authDao = new AuthDao(c))
		{
			Collection<Role> roles = roleDao.search();
			Collection<User> users = userDao.search();
			Collection<Auth> auths = authDao.search();

			for (Role role : roles)
			{
				for (Auth auth : auths)
					if (role.getId().equals(auth.getRole().getId()))
						role.getAuths().add(auth);

				for (User user : users)
					if (role.getId().equals(user.getRole().getId()))
						role.getUsers().add(user);

				for (Role item : roles)
					if (role.getId().equals(item.getRole().getId()))
						role.getRoles().add(item);
			}

			for (User user : users)
				for (Auth auth : auths)
					if (user.getId().equals(auth.getUser().getId()))
						user.getAuths().add(auth);

			Iterator<Role> iter = roles.iterator();
			while (iter.hasNext())
				if (iter.next().getRole().getId() != null)
					iter.remove();

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

	public void insert(Role model) throws AppException
	{
		Constraints.validate(model, "master", "active", "name", "email", "description", "roleID");
		try (RoleDao dao = new RoleDao())
		{
			dao.insert(model);
		}
	}

	public Role update(Role model) throws AppException
	{
		Constraints.validate(model, "master", "active", "name", "email", "description", "roleID");

		try (RoleDao dao = new RoleDao())
		{
			if (dao.update(model) == null)
				throw new AppException("Tentativa de alterar um GRUPO inexistente.");
			return model;
		}
	}

	public void delete(Role role) throws AppException
	{

		try (RoleDao dao = new RoleDao())
		{
			if (!dao.delete(role))
				throw new AppException("Tentativa de remover um GRUPO inexistente.");
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
