package gate.producer;

import gate.annotation.Current;
import gate.annotation.DataSource;
import gate.base.Control;
import gate.base.Dao;
import gate.base.Screen;
import gate.entity.App;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.LinkSource;
import gate.sql.condition.Condition;
import gate.sql.delete.Delete;
import gate.sql.insert.Insert;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import org.slf4j.Logger;

/**
 *
 * @author davins
 *
 * Produces an App object with current application data.
 *
 * Produces a Collection of App objects with all current gate based applications deployed on the container.
 *
 */
@ApplicationScoped
public class AppProducer implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Inject
	private ServletContext servletContext;

	private App app;

	@Inject
	private Instance<Screen> instances;

	@Inject
	private Logger logger;

	@Inject
	AppControl control;

	@PostConstruct
	public void prepare()
	{
		app = App.getInstance(Stream.of(servletContext.getServletContextName(),
			servletContext.getInitParameter("id")).filter(Objects::nonNull)
			.findFirst().orElseThrow(),
			servletContext.getInitParameter("name"),
			servletContext.getInitParameter("description"),
			instances.stream().map(e -> (Class<Screen>) e.getClass().getSuperclass())
				.collect(Collectors.toList()));

		try
		{
			control.update(app);
		} catch (ConstraintViolationException ex)
		{
			logger.error("Erro trying to update app data " + ex.getMessage(), ex);
		}
	}

	@Current
	@Produces
	@Named("app")
	public App produce()
	{
		return app;
	}

	@Dependent
	private static class AppControl extends Control
	{

		@Inject
		@DataSource("Gate")
		LinkSource linksource;

		public void update(App app)
			throws ConstraintViolationException
		{
			try ( Link link = linksource.getLink();
				 AppDao dao = new AppDao(link))
			{
				link.beginTran();
				dao.delete(app);
				dao.insert(app);
				link.commit();
			}
		}

		private static class AppDao extends Dao
		{

			public AppDao(Link link)
			{
				super(link);
			}

			public void insert(App app) throws ConstraintViolationException
			{
				Insert.into("App")
					.set("id", app.getId())
					.set("json", app.toString())
					.build()
					.connect(getLink())
					.execute();
			}

			public void delete(App app) throws ConstraintViolationException
			{
				Delete.from("App")
					.where(Condition.of("id").eq(app.getId()))
					.build()
					.connect(getLink())
					.execute();
			}

		}

	}
}
