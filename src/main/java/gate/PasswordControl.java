package gate;

import gate.entity.User;
import gate.error.*;
import gate.security.hash.BCrypt;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.sql.update.Update;
import jakarta.enterprise.context.Dependent;

@Dependent
public class PasswordControl extends gate.base.Control
{

	public User select(String username) throws InvalidUsernamePasswordException
	{
		try (Link link = Link.of("Gate");
		     PasswordDao dao = new PasswordDao(link))
		{
			return dao.select(username);
		}
	}

	public void update(User user, String password)
			throws BadRequestException, NotFoundException, ConstraintViolationException, InvalidCredentialsException
	{
		try (Link link = Link.of("Gate");
		     PasswordDao dao = new PasswordDao(link))
		{
			if (password == null || password.isBlank())
				throw new BadRequestException("Sua senha tem que ter no mínimo 8 caracteres");
			if (password.equals(user.getUsername()))
				throw new BadRequestException("Sua senha não pode ser igual ao seu login");
			if (password.equals(user.getEmail()))
				throw new BadRequestException("Sua senha não pode ser igual ao seu email");
			dao.update(user, BCrypt.digest(password));
		}
	}

	public static class PasswordDao extends gate.base.Dao
	{

		public PasswordDao(Link link)
		{
			super(link);
		}

		public User select(String username) throws InvalidUsernamePasswordException
		{
			return Select.expression("id")
					.expression("username")
					.expression("password")
					.expression("email")
					.from("gate.Uzer")
					.where(Condition.of("username")
							.eq(username)
							.or("email")
							.eq(username))
					.build()
					.connect(getLink())
					.fetchEntity(User.class)
					.orElseThrow(InvalidUsernamePasswordException::new);
		}

		public void update(User user, BCrypt password) throws NotFoundException, ConstraintViolationException
		{
			Update.table("gate.Uzer")
					.set("password", password)
					.where(Condition.of("id").eq(user.getId()))
					.build()
					.connect(getLink())
					.orElseThrow(NotFoundException::new);
		}
	}

}