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
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Objects;
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
		String id = Objects.requireNonNullElse(servletContext.getServletContextName(), servletContext.getInitParameter("id"));

		var types = instances.stream()
			.map(e -> (Class<Screen>) e.getClass())
			.map(e -> e.isSynthetic() ? e.getSuperclass() : e)
			.filter(type -> !Modifier.isAbstract(type.getModifiers()))
			.filter(type -> type.getSimpleName().endsWith("Screen"))
			.toList();

		app = App.getInstance(id, servletContext.getInitParameter("name"), servletContext.getInitParameter("description"), types);

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
			try (Link link = linksource.getLink();
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
