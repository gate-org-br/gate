package gate.thymeleaf;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.thymeleaf.context.IWebContext;

public class CDIWebContext extends CDIContext implements IWebContext
{

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final ServletContext servletContext;

	public CDIWebContext(final HttpServletRequest request, final HttpServletResponse response,
		final BeanManager beanManager, final ServletContext servletContext)
	{
		super(request.getLocale(), beanManager);
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;
	}

	@Override
	public HttpServletRequest getRequest()
	{
		return request;
	}

	@Override
	public HttpServletResponse getResponse()
	{
		return response;
	}

	@Override
	public HttpSession getSession()
	{
		return request.getSession(false);
	}

	@Override
	public ServletContext getServletContext()
	{
		return servletContext;
	}
}
