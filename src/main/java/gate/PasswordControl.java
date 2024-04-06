package gate;

import gate.annotation.DataSource;
import gate.entity.User;
import gate.error.BadRequestException;
import gate.error.ConstraintViolationException;
import gate.error.InvalidCredentialsException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;
import gate.type.MD5;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class PasswordControl extends gate.base.Control
{

	@Inject
	@DataSource(value = "Gate")
	LinkSource linksource;

	public User select(String username) throws InvalidUsernameException
	{
		try (Link link = linksource.getLink();
			PasswordDao dao = new PasswordDao(link))
		{
			return dao.select(username);
		}
	}

	public void update(User user, String password) throws BadRequestException, NotFoundException, ConstraintViolationException, InvalidCredentialsException
	{
		try (Link link = linksource.getLink();
			PasswordDao dao = new PasswordDao(link))
		{
			user = dao.select(user.getId());
			if (password == null || password.isBlank())
				throw new BadRequestException("Sua senha tem que ter no mínimo 8 caracteres");
			if (password.equals(user.getUsername()))
				throw new BadRequestException("Sua senha não pode ser igual ao seu login");
			if (password.equals(user.getEmail()))
				throw new BadRequestException("Sua senha não pode ser igual ao seu email");
			dao.update(user, password);
		}
	}

	public static class PasswordDao extends gate.base.Dao
	{

		public PasswordDao(Link link)
		{
			super(link);
		}

		public User select(ID id) throws InvalidCredentialsException
		{
			return Select.expression("id")
				.expression("username")
				.expression("password")
				.expression("email")
				.from("gate.Uzer")
				.where(Condition.of("id").eq(id))
				.build().connect(getLink())
				.fetchEntity(User.class)
				.orElseThrow(InvalidCredentialsException::new);
		}

		public User select(String username) throws InvalidUsernameException
		{
			return Select.expression("id")
				.expression("username")
				.expression("password")
				.expression("email")
				.from("gate.Uzer")
				.where(Condition.of("username").eq(username)
					.or("email").eq(username))
				.build().connect(getLink())
				.fetchEntity(User.class)
				.orElseThrow(InvalidUsernameException::new);
		}

		public void update(User user, String password) throws NotFoundException, ConstraintViolationException
		{
			Update.table("gate.Uzer")
				.set("password", MD5.digest(password))
				.where(Condition.of("id").eq(user.getId()))
				.build().connect(getLink())
				.orElseThrow(NotFoundException::new);
		}
	}

}
