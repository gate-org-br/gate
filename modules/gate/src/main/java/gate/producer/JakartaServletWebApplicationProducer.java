package gate.producer;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@RequestScoped
public class JakartaServletWebApplicationProducer
{

	@Inject
	HttpServletRequest request;

	private JakartaServletWebApplication jakartaServletWebApplication;

	@PostConstruct
	public void build()
	{
		jakartaServletWebApplication
			= JakartaServletWebApplication.buildApplication(request.getServletContext());
	}

	@Produces
	@Dependent
	public JakartaServletWebApplication produce(HttpServletRequest request)
	{
		return jakartaServletWebApplication;
	}
}
