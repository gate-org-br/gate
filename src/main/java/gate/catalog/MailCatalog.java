package gate.catalog;

import gate.base.Dao;
import gate.entity.App;
import gate.entity.Mail;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.delete.Delete;
import gate.sql.insert.Insert;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class MailCatalog
{
	public static List<Mail> search(App app, int maxAttempts, Duration retryInterval)
	{
		try (Link link = Link.of("Gate");
		     MailDao dao = new MailDao(link))
		{
			return dao.search(app, maxAttempts, retryInterval.toSeconds());
		}
	}


	public static void insert(Mail mail)
	{
		try (Link link = Link.of("Gate");
		     MailDao dao = new MailDao(link))
		{
			dao.insert(mail);
		}
	}

	public static void update(Mail mail)
	{
		try (Link link = Link.of("Gate");
		     MailDao dao = new MailDao(link))
		{
			dao.update(mail);
		}
	}

	public static void expire(App app, int maxAttempts)
	{
		try (Link link = Link.of("Gate");
		     MailDao dao = new MailDao(link))
		{
			dao.expire(app, maxAttempts);
		}
	}

	public static void delete(Mail mail)
	{
		try (Link link = Link.of("Gate");
		     MailDao dao = new MailDao(link))
		{
			dao.delete(mail);
		}
	}

	static class MailDao extends Dao
	{

		public MailDao(Link link)
		{
			super(link);
		}


		public List<Mail> search(App app, int maxAttempts, long retryIntervalSeconds)
		{
			return Select
					.expression("id")
					.expression("app")
					.expression("date")
					.expression("sender")
					.expression("message")
					.expression("attempts")
					.expression("receiver")
					.expression("expiration")
					.from("Mail")
					.where(Condition.of("app").eq(app.getId())
							.and("attempts").lt(maxAttempts)
							.and("expiration").ge(LocalDateTime.now())
							.and("UNIX_TIMESTAMP(date) + attempts * " + retryIntervalSeconds)
							.isLe("UNIX_TIMESTAMP()"))
					.orderBy("attempts").and("date")
					.build()
					.connect(getLink())
					.fetchEntityList(Mail.class);
		}

		public void insert(Mail mail)
		{
			Insert.into("Mail")
					.set("app", mail.getApp())
					.set("date", mail.getDate())
					.set("sender", mail.getSender())
					.set("message", mail.getMessage())
					.set("attempts", mail.getAttempts())
					.set("receiver", mail.getReceiver())
					.set("expiration", mail.getExpiration())
					.build()
					.connect(getLink())
					.fetchGeneratedKey(ID.class).ifPresent(mail::setId);
		}

		public void update(Mail mail)
		{
			Update.table("Mail")
					.set("attempts", mail.getAttempts())
					.where(Condition.of("id").eq(mail.getId()))
					.build()
					.connect(getLink())
					.orElseThrow(NotFoundException::new);
		}

		public void expire(App app, int maxAttempts)
		{
			Delete.from("Mail")
					.where(Condition.of("app").eq(app.getId())
							.and(Condition.of("expiration").lt(LocalDateTime.now())
									.or("attempts").ge(maxAttempts)))
					.build()
					.connect(getLink())
					.execute();
		}

		public void delete(Mail mail)
		{
			Delete.from("Mail")
					.where(Condition.of("id").eq(mail.getId()))
					.build()
					.connect(getLink())
					.orElseThrow(NotFoundException::new);
		}
	}
}