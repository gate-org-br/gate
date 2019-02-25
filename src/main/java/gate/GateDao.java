package gate;

import gate.entity.Auth;
import gate.entity.Bond;
import gate.entity.Role;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

class GateDao extends gate.base.Dao
{

	public GateDao()
	{
		super("Gate");
	}

	public Optional<User> getUser(String username)
	{
		return getLink().from(getClass().getResource("getUser(String).sql"))
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

	public boolean update(User user) throws ConstraintViolationException
	{
		return getLink().prepare("update Uzer set passwd = MD5(?) where userId = ? and passwd = MD5(?)").parameters(user.getChange(), user.getUserID(), user.getPasswd()).execute() > 0;
	}

}
