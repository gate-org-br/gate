package gate;

import gate.base.Dao;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.error.InvalidCredentialsException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;

public class PasswordDao extends Dao
{

	public PasswordDao(Link link)
	{
		super(link);
	}

	public User select(ID id) throws InvalidCredentialsException
	{
		return Select.expression("id").expression("username").expression("password").expression("email")
				.from("gate.Uzer").where(Condition.of("id").eq(id)).build().connect(getLink()).fetchEntity(User.class)
				.orElseThrow(InvalidCredentialsException::new);
	}

	public User select(String username) throws InvalidUsernameException
	{
		return Select.expression("id").expression("username").expression("password").expression("email")
				.from("gate.Uzer").where(Condition.of("username").eq(username).or("email").eq(username)).build()
				.connect(getLink()).fetchEntity(User.class).orElseThrow(InvalidUsernameException::new);
	}

	public void update(User user, String password) throws NotFoundException, ConstraintViolationException
	{
		Update.table("gate.Uzer").set("password", password).where(Condition.of("id").eq(user.getId())).build()
				.connect(getLink()).orElseThrow(NotFoundException::new);
	}

}
