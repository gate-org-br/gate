package gate;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.error.InvalidUsernameException;
import gate.sql.Cursor;
import gate.sql.Link;
import gate.sql.fetcher.Fetcher;
import gate.type.ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class GateDao extends gate.base.Dao
{

	public GateDao(Link link)
	{
		super(link);
	}

	public User select(ID id) throws InvalidUsernameException
	{
		return getLink().from(getClass().getResource("select(ID).sql"))
				.parameters(id, id, id)
				.fetch(new UzerFetcher())
				.orElseThrow(InvalidUsernameException::new);
	}

	public User select(String username) throws InvalidUsernameException
	{
		return getLink().from(getClass().getResource("select(String).sql"))
				.parameters(username, username, username, username, username, username)
				.fetch(new UzerFetcher())
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

	private static class UzerFetcher implements Fetcher<Optional<User>>
	{

		@Override
		public Optional<User> fetch(Cursor cursor)
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
		}

	}
}