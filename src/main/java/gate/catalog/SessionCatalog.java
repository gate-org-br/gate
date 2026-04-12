package gate.catalog;

import gate.entity.Session;
import gate.security.Credentials;
import gate.security.SessionPolicy;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.delete.Delete;
import gate.sql.replace.Replace;
import gate.sql.select.Select;
import gate.type.ID;

import java.time.LocalDateTime;

public class SessionCatalog
{

	public static boolean exists(Credentials credentials)
	{
		try (Link link = Link.of("Gate");
		     SessionDao dao = new SessionDao(link))
		{
			return dao.exists(credentials);
		}
	}

	public static String create(gate.entity.User user)
	{
		try (Link link = Link.of("Gate");
		     SessionDao dao = new SessionDao(link);
		     UserCatalog.UserDao userDao = new UserCatalog.UserDao(link))
		{
			link.beginTran();
			userDao.update(user, gate.entity.User::getActivity, LocalDateTime.now());

			var sub = user.getId();
			var sid = SessionPolicy.CURRENT.revocable()
					? dao.insert(new Session()
								 .setUser(user)
								 .setDate(LocalDateTime.now()))
					  .getId()
					: null;
			user = SessionPolicy.CURRENT.statelessToken() ? user : null;
			link.commit();
			return Credentials.create(sub, sid, user).toString();
		}
	}

	public static void revoke(String token)
	{
		var credentials = Credentials.parse(token);
		try (Link link = Link.of("Gate");
		     SessionDao dao = new SessionDao(link);
		     UserCatalog.UserDao userDao = new UserCatalog.UserDao(link))
		{
			link.beginTran();
			if (credentials.revocable())
				dao.delete(new Session().setId(credentials.sid()));
			userDao.update(new gate.entity.User().setId(credentials.sub()),
					gate.entity.User::getActivity, LocalDateTime.now());
			link.commit();
		}
	}

	private static class SessionDao extends gate.base.Dao
	{

		public SessionDao(Link link)
		{
			super(link);
		}

		public Session insert(Session session)
		{
			Replace.into(session)
					.set(Session::getUser)
					.set(Session::getDate)
					.build()
					.connect(getLink())
					.fetchGeneratedKey(ID.class)
					.ifPresent(session::setId);
			return session;
		}

		public boolean exists(Credentials credentials)
		{
			return Select.exists(Select.expression("*")
							.from("Session")
							.where(Condition.of("id")
									.eq(credentials.sid())
									.and("Uzer$id").eq(credentials.sub())))
					.build()
					.connect(getLink())
					.fetchBoolean();
		}

		public void delete(Session session)
		{
			Delete.from("Session")
					.where(Condition.of("id").eq(session.getId()))
					.build()
					.connect(getLink())
					.execute();
		}
	}
}