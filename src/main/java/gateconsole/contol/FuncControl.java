package gateconsole.contol;

import gate.base.Control;
import gate.constraint.Constraints;
import gate.entity.Func;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.type.ID;
import gateconsole.dao.FuncDao;
import java.util.List;
import javax.enterprise.context.Dependent;
import gate.annotation.CopyInfo;

@Dependent
@CopyInfo(User.class)
public class FuncControl extends Control
{

	public List<Func> search()
	{
		try (FuncDao dao = new FuncDao())
		{
			return dao.search();
		}
	}

	public List<Func> search(Func filter)
	{
		try (FuncDao dao = new FuncDao())
		{
			return dao.search(filter);
		}
	}

	public Func select(ID id) throws AppException
	{
		try (FuncDao dao = new FuncDao())
		{
			return dao.select(id);
		}
	}

	public void insert(Func func) throws AppException
	{
		Constraints.validate(func, "name");

		try (FuncDao dao = new FuncDao())
		{
			dao.insert(func);
		}
	}

	public void update(Func func) throws AppException
	{
		Constraints.validate(func, "name");

		try (FuncDao dao = new FuncDao())
		{
			dao.update(func);
		}
	}

	public void delete(Func user) throws AppException
	{
		try (FuncDao dao = new FuncDao())
		{
			dao.delete(user);
		}
	}

	public static class UserControl extends Control
	{

		public List<Func> search(User user)
		{
			try (FuncDao.UserDao dao = new FuncDao.UserDao())
			{
				return dao.search(user);
			}
		}

		public void insert(Func func, User user) throws AppException
		{
			try (FuncDao.UserDao dao = new FuncDao.UserDao())
			{
				dao.insert(func, user);
			}
		}

		public void delete(Func func, User user) throws AppException
		{
			try (FuncDao.UserDao dao = new FuncDao.UserDao())
			{
				dao.delete(func, user);
			}
		}
	}

	public static class RoleControl extends Control
	{

		public List<Func> search(Role role)
		{
			try (FuncDao.RoleDao dao = new FuncDao.RoleDao())
			{
				return dao.search(role);
			}
		}

		public void insert(Func func, Role role) throws AppException
		{
			try (FuncDao.RoleDao dao = new FuncDao.RoleDao())
			{
				dao.insert(func, role);
			}
		}

		public void delete(Func func, Role role) throws AppException
		{
			try (FuncDao.RoleDao dao = new FuncDao.RoleDao())
			{
				dao.delete(func, role);
			}
		}
	}

}
