package gate.producer;

import gate.CallRegistry;
import gate.annotation.Current;
import gate.annotation.LinkResource;
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
import gate.util.SystemProperty;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 *
 * @author davins
 * <p>
 * Produces an App object with current application data.
 * <p>
 * Produces a Collection of App objects with all current gate based applications
 * deployed on the container.
 *
 */
@ApplicationScoped
public class AppProducer implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	private App app;

	@Inject
	private Logger logger;

	@Inject
	private Instance<Screen> instances;

	@Inject
	AppControl control;

	@Inject
	CallRegistry actionRegistry;

	private static final String ID = SystemProperty.get("gate.app.id").orElse("default");
	private static final String NAME = SystemProperty.get("gate.app.name").orElse("No app name provided");
	private static final String DESCRIPTION = SystemProperty.get("gate.app.description")
			.orElse("No description provided");

	@PostConstruct
	public void prepare()
	{

		@SuppressWarnings("unchecked")
		var types = instances.stream().map(e -> (Class<Screen>) e.getClass())
				.map(e -> e.isSynthetic() ? e.getSuperclass() : e)
				.filter(type -> !Modifier.isAbstract(type.getModifiers()))
				.filter(type -> type.getSimpleName().endsWith("Screen"))
				.filter(Screen.class::isAssignableFrom)
				.map(e -> (Class<Screen>) e)
				.toList();

		app = App.getInstance(ID, NAME, DESCRIPTION, types);

		try
		{
			control.update(app);

			actionRegistry.register(types);
		} catch (ConstraintViolationException ex)
		{
			logger.error("Erro trying to update app data {}", ex.getMessage(), ex);
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
		@LinkResource("Gate")
		LinkSource linkSource;

		public void update(App app) throws ConstraintViolationException
		{
			try (Link link = linkSource.getLink();
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
						.connect(getLink()).execute();
			}

			public void delete(App app) throws ConstraintViolationException
			{
				Delete.from("App")
						.where(Condition.of("id")
								.eq(app.getId()))
						.build()
						.connect(getLink()).execute();
			}
		}
	}
}