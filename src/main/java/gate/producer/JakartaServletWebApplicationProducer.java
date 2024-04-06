package gate.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

public class JakartaServletWebApplicationProducer
{

	@Inject
	ServletContext context;

	private JakartaServletWebApplication instance;

	@Produces
	@ApplicationScoped
	public JakartaServletWebApplication produce()
	{
		if (instance == null)
			instance = JakartaServletWebApplication.buildApplication(context);
		return instance;
	}
}
