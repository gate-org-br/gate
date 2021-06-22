package gate.thymeleaf;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.thymeleaf.context.IWebContext;

/**
 * Extension of the basic CDI context for use in web enviroments.
 *
 * CDI functionality is inherited unchanged from {@link CDIContext}. Additional methods are added to implement the Thymeleaf {@link IWebContext} interface. The
 * context locale cannot be set directly, it is taken from the servlet request.
 *
 * @author PÁLFALVI Tamás &lt;tamas.palfalvi@inbuss.hu&gt;
 */
public class CDIWebContext extends CDIContext implements IWebContext
{

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final ServletContext servletContext;

	protected CDIWebContext(final HttpServletRequest request, final HttpServletResponse response,
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
