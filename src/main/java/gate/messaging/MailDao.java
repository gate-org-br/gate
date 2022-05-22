package gate.messaging;

import gate.base.Dao;
import gate.entity.App;
import gate.entity.Mail;
import gate.entity.Server;
import gate.error.AppException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.condition.Condition;
import gate.sql.delete.Delete;
import gate.sql.insert.Insert;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class MailDao extends Dao
{

	public MailDao(Link link)
	{
		super(link);
	}

	public boolean isEnabled()
	{
		return getLink()
			.from("select exists (select * from Server where type = ?)")
			.parameters(Server.Type.SMTP)
			.fetchBoolean();
	}

	public Server server() throws AppException
	{
		return Select.expression("type")
			.expression("host")
			.expression("port")
			.expression("username")
			.expression("password")
			.from("Server")
			.where(Condition.of("type").eq(Server.Type.SMTP))
			.build()
			.connect(getLink())
			.fetchEntity(Server.class)
			.orElseThrow(() -> new AppException("Nenhum servidor SMTP configurado"));
	}

	public List<Mail> search()
	{
		return Select
			.expression("id")
			.expression("app")
			.expression("date")
			.expression("sender")
			.expression("receiver")
			.expression("attempts")
			.expression("expiration")
			.from("Mail")
			.orderBy("date")
			.limit(1)
			.build()
			.connect(getLink())
			.fetchEntityList(Mail.class);
	}

	public List<Mail> search(App app)
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
				.and("date_add(Mail.date, interval Mail.attempts hour)").le(LocalDateTime.now()))
			.orderBy("attempts").and("date")
			.build()
			.connect(getLink())
			.fetchEntityList(Mail.class);
	}

	public Optional<Mail> select(App app)
	{
		return Select
			.expression("id")
			.expression("date")
			.expression("sender")
			.expression("message")
			.expression("attempts")
			.expression("receiver")
			.expression("expiration")
			.from("Mail")
			.where(Condition.of("app").eq(app.getId())
				.and("date_add(Mail.date, interval Mail.attempts hour)").le(LocalDateTime.now()))
			.orderBy("attempts").and("date")
			.limit(1)
			.build()
			.connect(getLink())
			.fetchEntity(Mail.class);
	}

	public void insert(Mail mail) throws AppException
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

	public void update(Mail mail) throws AppException
	{
		if (Update.table("Mail")
			.set("attempts", mail.getAttempts())
			.where(Condition.of("id").eq(mail.getId()))
			.build()
			.connect(getLink())
			.execute() == 0)
			throw new NotFoundException();
	}

	public void expire(App app) throws AppException
	{
		Delete.from("Mail")
			.where(Condition.of("app").eq(app.getId())
				.and("expiration").lt(LocalDateTime.now()))
			.build()
			.connect(getLink())
			.execute();
	}

	public void delete(Mail mail) throws AppException
	{
		if (Delete.from("Mail")
			.where(Condition.of("id").eq(mail.getId()))
			.build()
			.connect(getLink())
			.execute() == 0)
			throw new NotFoundException();

	}
}
