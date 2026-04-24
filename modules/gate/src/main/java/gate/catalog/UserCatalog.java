package gate.catalog;

import gate.annotation.LinkResource;
import gate.entity.Auth;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernamePasswordException;
import gate.error.NotFoundException;
import gate.sql.Cursor;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.sql.condition.Condition;
import gate.sql.fetcher.Fetcher;
import gate.sql.update.Update;
import gate.type.Hierarchy;
import gate.type.ID;
import gate.type.PropertyReference;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserCatalog
{
	@Inject
	@LinkResource("Gate")
	LinkSource linkSource;

	public User select(ID id)
	{
		try (Link link = linkSource.get();
		     UserDao dao = new UserDao(link);
		     RoleCatalog.RoleDao roleDao = new RoleCatalog.RoleDao(link))
		{
			User user = dao.select(id);

			if (user.isDisabled())
				throw new InvalidUsernamePasswordException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernamePasswordException();

			var roles = roleDao.search();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(user.getRole()::equals).findAny()
					.orElseThrow(() -> new HierarchyException("Users role not found")));
			return user;
		}
	}

	public User select(String username)
	{
		if (Toolkit.isEmpty(username) || username.length() > 64)
			throw new InvalidUsernamePasswordException();

		try (Link link = linkSource.get();
		     RoleCatalog.RoleDao roleDao = new RoleCatalog.RoleDao(link);
		     UserDao dao = new UserDao(link))
		{
			User user = dao.select(username);

			if (user.isDisabled())
				throw new InvalidUsernamePasswordException();
			if (user.getRole().getId() == null)
				throw new InvalidUsernamePasswordException();

			var roles = roleDao.search();
			Hierarchy.setup(roles);
			user.setRole(roles.stream().filter(user.getRole()::equals).findAny()
					.orElseThrow(() -> new HierarchyException("Users role not found")));
			return user;
		}
	}

	public void update(User user,
	                   PropertyReference<User, Object> property,
	                   Object value)
	{
		try (Link link = linkSource.get();
		     UserDao dao = new UserDao(link))
		{
			dao.update(user, property, value);
		}
	}

	static class UserDao extends gate.base.Dao
	{

		public UserDao(Link link)
		{
			super(link);
		}

		public gate.entity.User select(ID id)
		{
			return getLink().from(getClass().getResource("/gate/catalog/UserCatalog/select(ID).sql"), List.of(id, id, id))
					.fetch(new UzerFetcher())
					.orElseThrow(InvalidUsernamePasswordException::new);
		}

		public gate.entity.User select(String username)
		{
			return getLink().from(
							getClass().getResource("/gate/catalog/UserCatalog/select(String).sql"),
							List.of(username, username, username, username, username, username))
					.fetch(new UzerFetcher())
					.orElseThrow(InvalidUsernamePasswordException::new);
		}

		public void update(gate.entity.User user, PropertyReference<gate.entity.User, Object> propertyReference, Object value)
		{
			Update.table(gate.entity.User.class)
					.set(propertyReference, value)
					.where(Condition.of(gate.entity.User::getId).eq(user.getId()))
					.build()
					.connect(getLink())
					.orElseThrow(NotFoundException::new);
		}


		private static class UzerFetcher implements Fetcher<Optional<User>>
		{

			@Override
			public Optional<gate.entity.User> fetch(Cursor cursor)
			{
				if (cursor.next())
				{
					gate.entity.User user = new gate.entity.User();
					user.setId(cursor.getValue(ID.class, "id"));
					user.setActive(cursor.getValue(Boolean.class, "active"));
					user.getRole().setId(cursor.getValue(ID.class, "role.id"));
					user.setUsername(cursor.getValue(String.class, "username"));
					user.setPassword(cursor.getValue(String.class, "password"));
					user.setName(cursor.getValue(String.class, "name"));
					user.setEmail(cursor.getValue(String.class, "email"));

					while (cursor.next())
						user.getAuths().add(new gate.entity.Auth()
								.setId(cursor.getValue(ID.class, "auth.id"))
								.setScope(cursor.getValue(gate.entity.Auth.Scope.class, "auth.scope"))
								.setAccess(cursor.getValue(Auth.Access.class, "auth.access"))
								.setModule(cursor.getValue(String.class, "auth.module"))
								.setScreen(cursor.getValue(String.class, "auth.screen"))
								.setAction(cursor.getValue(String.class, "auth.action")));

					return Optional.of(user);
				}

				return Optional.empty();
			}

		}
	}
}