package gate;

import gate.entity.Auth;
import gate.entity.Bond;
import gate.entity.Role;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;
import gate.type.MD5;
import java.util.List;

class GateDao extends gate.base.Dao
{

	public GateDao(Link link)
	{
		super(link);
	}

	public User select(String username) throws InvalidUsernameException
	{
		return getLink().from(getClass().getResource("select(String).sql"))
			.parameters(username, username)
			.fetchEntity(User.class)
			.orElseThrow(InvalidUsernameException::new);
	}

	public User select(ID id) throws NotFoundException
	{
		return Select.expression("id")
			.expression("username")
			.expression("email")
			.from("gate.Uzer")
			.where(Condition.of("id").eq(id))
			.build()
			.connect(getLink())
			.fetchEntity(User.class)
			.orElseThrow(NotFoundException::new);
	}

	public List<Auth> getAuths()
	{
		return getLink()
			.from(getClass().getResource("getAuths().sql"))
			.constant()
			.fetchEntityList(Auth.class);
	}

	public List<Role> getRoles()
	{
		return getLink()
			.from(getClass().getResource("getRoles().sql"))
			.constant()
			.fetchEntityList(Role.class);
	}

	public List<Bond> getBonds()
	{
		return getLink()
			.from(getClass().getResource("getBonds().sql"))
			.constant()
			.fetchEntityList(Bond.class);
	}

	public void update(User user, String password) throws ConstraintViolationException, NotFoundException
	{
		Update.table("Uzer")
			.set("password", MD5.digest(password))
			.where(Condition.of("id").eq(user.getId()))
			.build()
			.connect(getLink()).orElseThrow(NotFoundException::new);
	}

}
