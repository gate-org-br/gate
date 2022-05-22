package gate.messaging;

import gate.annotation.DataSource;
import gate.base.Control;
import gate.entity.App;
import gate.entity.Mail;
import gate.entity.Server;
import gate.error.AppException;
import gate.sql.Link;
import gate.sql.LinkSource;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
class MailControl extends Control
{

	@Inject
	@DataSource("Gate")
	LinkSource linkSource;

	public boolean isEnabled()
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			return dao.isEnabled();
		}
	}

	public Server server() throws AppException
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			return dao.server();
		}
	}

	public List<Mail> search()
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			return dao.search();
		}
	}

	public List<Mail> search(App app)
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			return dao.search(app);
		}
	}

	public Optional<Mail> select(App app)
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			return dao.select(app);
		}
	}

	public void insert(Mail mail) throws AppException
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			dao.insert(mail);
		}
	}

	public void update(Mail mail) throws AppException
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			dao.update(mail);
		}
	}

	public void expire(App app) throws AppException
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			dao.expire(app);
		}
	}

	public void delete(Mail mail) throws AppException
	{
		try ( Link link = linkSource.getLink();
			 MailDao dao = new MailDao(link))
		{
			dao.delete(mail);
		}
	}
}
