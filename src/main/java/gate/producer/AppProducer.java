package gate.producer;

import gate.annotation.Current;
import gate.base.Screen;
import gate.entity.App;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
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
@ApplicationScoped
public class AppProducer implements Serializable
{

	private static final long serialVersionUID = 1L;

//	@Inject
//	@Name("gate.apps")
//	private JNDIContextMap<App> applications;
	@Inject
	private ServletContext servletContext;

	private App app;

	@Inject
	private Instance<Screen> instances;

	@PostConstruct
	public void prepare()
	{

		app = App.getInstance(servletContext.getServletContextName(),
			servletContext.getInitParameter("name"),
			servletContext.getInitParameter("description"),
			instances.stream().map(e -> e.getClass())
				.collect(Collectors.toList()));
		//applications.put(app.getId(), app);
	}

	@PreDestroy
	public void dispose()
	{
		//applications.remove(app.getId());
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
		return List.of();
		//return applications.values();
	}
}
