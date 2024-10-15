package gate;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.AppException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.update.Update;
import gate.type.ID;
import gate.type.MD5;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class GateDao extends gate.base.Dao
{

	public GateDao(Link link)
	{
		super(link);
	}

	public User select(String username) throws InvalidUsernameException
	{
		return (User) getLink().from(getClass().getResource("select(String).sql"))
				.parameters(username, username, username, username, username, username)
				.fetch(cursor ->
				{
					if (cursor.next())
					{
						User user = new User();
						user.setId(cursor.getValue(ID.class, "id"));
						user.setActive(cursor.getValue(Boolean.class, "active"));
						user.getRole().setId(cursor.getValue(ID.class, "role.id"));
						user.setUsername(cursor.getValue(String.class, "username"));
						user.setPassword(cursor.getValue(String.class, "password"));
						user.setName(cursor.getValue(String.class, "name"));
						user.setEmail(cursor.getValue(String.class, "email"));

						while (cursor.next())
						{
							Auth auth = new Auth();
							auth.setId(cursor.getValue(ID.class, "auth.id"));
							auth.setScope(cursor.getValue(Auth.Scope.class, "auth.scope"));
							auth.setAccess(cursor.getValue(Auth.Access.class, "auth.access"));
							auth.setModule(cursor.getValue(String.class, "auth.module"));
							auth.setScreen(cursor.getValue(String.class, "auth.screen"));
							auth.setAction(cursor.getValue(String.class, "auth.action"));
							user.getAuths().add(auth);
						}

						return Optional.of(user);
					}

					return Optional.empty();
				})
				.orElseThrow(InvalidUsernameException::new);
	}

	public List<Role> getRoles()
	{
		return getLink()
				.from(getClass().getResource("getRoles().sql"))
				.constant()
				.fetch(cursor ->
				{
					List<Role> roles = new ArrayList<>();
					if (cursor.next())
					{
						do
						{
							Role role = new Role();
							role.setId(cursor.getValue(ID.class, "id"));
							role.setActive(cursor.getValue(Boolean.class, "active"));
							role.setMaster(cursor.getValue(Boolean.class, "master"));
							role.setRolename(cursor.getValue(String.class, "rolename"));
							role.setName(cursor.getValue(String.class, "name"));
							role.setEmail(cursor.getValue(String.class, "email"));
							role.getRole().setId(cursor.getValue(ID.class, "role.id"));
							role.getManager().setId(cursor.getValue(ID.class, "manager.id"));
							role.getManager().setName(cursor.getValue(String.class, "manager.name"));

							while (cursor.next() && cursor.getValue(ID.class, "id").equals(role.getId()))
							{
								Auth auth = new Auth();
								auth.setId(cursor.getValue(ID.class, "auth.id"));
								auth.setScope(cursor.getValue(Auth.Scope.class, "auth.scope"));
								auth.setAccess(cursor.getValue(Auth.Access.class, "auth.access"));
								auth.setModule(cursor.getValue(String.class, "auth.module"));
								auth.setScreen(cursor.getValue(String.class, "auth.screen"));
								auth.setAction(cursor.getValue(String.class, "auth.action"));
								role.getAuths().add(auth);
							}

							roles.add(role);
						} while (!cursor.isAfterLast());
					}
					return roles;
				});
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
