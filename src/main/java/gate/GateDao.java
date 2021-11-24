package gate;

import gate.entity.Auth;
import gate.entity.Bond;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.update.Update;
import gate.type.MD5;
import java.util.List;
import java.util.Optional;

class GateDao extends gate.base.Dao
{

	public GateDao(Link link)
	{
		super(link);
	}

	public Optional<User> select(String username)
	{
		return getLink().from(getClass().getResource("select(String).sql"))
			.parameters(username, username)
			.fetchEntity(User.class);
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

	public void update(User user, String password) throws AppException
	{
		if (Update.table("Uzer")
			.set("password", MD5.digest(password))
			.where(Condition.of("id").eq(user.getId()))
			.build()
			.connect(getLink())
			.execute() == 0)
			throw new NotFoundException();
	}

	public void update(User user) throws AppException
	{
		if (Update.table("Uzer")
			.set("password", MD5.digest(user.getChange()))
			.where(Condition.of("username").eq(user.getUsername())
				.and("password").eq(MD5.digest(user.getPassword())))
			.build()
			.connect(getLink())
			.execute() == 0)
			throw new NotFoundException("Usuário se senha inválidos.");
	}

}
