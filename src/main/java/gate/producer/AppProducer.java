package gate.producer;

import gate.annotation.Current;
import gate.annotation.Name;
import gate.entity.App;
import gate.util.ContextMap;
import java.io.Serializable;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

/**
 *
 * @author davins
 *
 * Produces an App object with current application data.
 *
 * Produces a Collection of App objects with all current gate based applications deployed on the container.
 *
 */
@Startup
@Singleton
public class AppProducer implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Inject
	@Name("gate.apps")
	private ContextMap<App> applications;

	@Inject
	private ServletContext servletContext;

	private App app;

	@PostConstruct
	public void prepare()
	{
		app = App.getInstance(servletContext);
		applications.put(app.getId(), app);
	}

	@PreDestroy
	public void dispose()
	{
		applications.remove(app.getId());
	}

	@Current
	@Produces
	@Named("app")
	public App produce()
	{
		return app;
	}

	@Current
	@Produces
	@Named("apps")
	public Collection<App> getAll()
	{
		return applications.values();
	}
}
