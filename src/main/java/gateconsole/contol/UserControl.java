package gateconsole.contol;

import gate.Progress;
import gate.base.Control;
import gate.constraint.Constraints;
import gate.entity.Func;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.type.ID;
import gate.type.mime.MimeData;
import gateconsole.dao.UserDao;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;

@Dependent
public class UserControl extends Control
{

	private static final int MAX_PHOTO_SIZE = 65535;

	public List<User> getSubscriptions()
	{
		try (UserDao dao = new UserDao())
		{
			return dao.getSubscriptions();
		}
	}

	public List<User> search()
	{
		try (UserDao dao = new UserDao())
		{
			return dao.search().stream().peek(e -> e.setRole(getUser().getRole().getRoot().select(e.getRole().getId())))
				.collect(Collectors.toList());
		}
	}

	public List<User> search(User filter)
	{
		try (UserDao dao = new UserDao())
		{
			return dao.search(filter).stream()
				.peek(e -> e.setRole(getUser().getRole().getRoot()
				.select(e.getRole()
					.getId())))
				.collect(Collectors.toList());
		}
	}

	public User select(ID id) throws AppException
	{
		try (UserDao dao = new UserDao())
		{
			User user = dao.select(id);
			if (user.getRole().getId() != null)
				user.setRole(getUser().getRole().getRoot().select(user.getRole().getId()));
			return user;
		}
	}

	public void insert(User value) throws AppException
	{
		Constraints.validate(value, "active", "userID", "name", "email", "details", "phone", "cellPhone", "CPF");

		if (value.getPhoto() != null && value.getPhoto().getSize() > MAX_PHOTO_SIZE)
			throw new AppException("Fotos devem possuir no máximo %d bytes", MAX_PHOTO_SIZE);
		try (UserDao dao = new UserDao())
		{
			if (value.getPasswd() == null)
				value.setPasswd(value.getUserID()).toString();
			dao.insert(value);
		}
	}

	public void insert(Role role, List<User> values) throws AppException
	{
		Progress.startup(values.size(), "Inserindo usuários");

		try (UserDao dao = new UserDao())
		{
			dao.getLink().beginTran();
			for (User value : values)
			{
				value.setRole(role);
				value.setActive(true);

				Constraints.validate(value, "userID", "name", "email", "details", "phone", "cellPhone", "CPF");

				if (value.getPhoto() != null && value.getPhoto().getSize() > MAX_PHOTO_SIZE)
					throw new AppException("Fotos devem possuir no máximo %d bytes", MAX_PHOTO_SIZE);

				if (value.getPasswd() == null)
					value.setPasswd(value.getUserID());

				try
				{
					dao.insert(value);
				} catch (ConstraintViolationException ex)
				{
					if (value.getEmail() == null)
						throw new AppException(ex, "%s: %s", ex.getMessage(), value.getUserID());
					else
						throw new AppException(ex, "%s: %s (%s)", ex.getMessage(), value.getUserID(), value.getEmail());
				}

				Progress.update(value.getName() + " inserido com sucesso");
			}
			dao.getLink().commit();
			Progress.commit("Todos os usuários foram inseridos com sucesso");
		}
	}

	public MimeData getPhoto(ID id)
	{
		try (UserDao dao = new UserDao())
		{
			return dao.getPhoto(id);
		}
	}

	public void update(User model) throws AppException
	{
		Constraints.validate(model, "active", "role.id",
			"userID", "name", "email", "details", "phone",
			"cellPhone", "photo", "CPF");

		if (model.getPhoto() != null && model.getPhoto().getSize() > 65535)
			throw new AppException("Fotos devem possuir no máximo %d bytes", MAX_PHOTO_SIZE);
		try (UserDao dao = new UserDao())
		{
			dao.update(model);
		}
	}

	public void accept(User model, Role role) throws AppException
	{
		try (UserDao dao = new UserDao())
		{
			dao.accept(model, role);
		}
	}

	public void delete(User user) throws AppException
	{
		try (UserDao dao = new UserDao())
		{
			if (!dao.delete(user))
				throw new AppException("Tentativa de remover um USUÁRIO inexistente.");
		}
	}

	public void passwd(User user) throws AppException
	{
		try (UserDao dao = new UserDao())
		{
			if (!dao.setPasswd(user))
				throw new AppException("Tentativa de resetar a senha de um USUÁRIO inexistente.");
		}
	}

	public static class FuncControl extends Control
	{

		public List<User> search(Func func)
		{
			try (UserDao.FuncDao dao = new UserDao.FuncDao())
			{
				return dao.search(func);
			}
		}

		public void insert(User user, Func func) throws AppException
		{
			try (UserDao.FuncDao dao = new UserDao.FuncDao())
			{
				dao.insert(user, func);
			}

		}

		public void delete(User user, Func func) throws AppException
		{
			try (UserDao.FuncDao dao = new UserDao.FuncDao())
			{
				dao.delete(user, func);
			}
		}
	}
}
