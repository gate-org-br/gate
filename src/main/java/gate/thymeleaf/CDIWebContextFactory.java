package gate.thymeleaf;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.context.IWebContext;

/**
 * Factory bean for creating {@link CDIWebContext} instances.
 *
 * @author PÁLFALVI Tamás &lt;tamas.palfalvi@inbuss.hu&gt;
 */
@Dependent
public class CDIWebContextFactory
{

	private final BeanManager beanManager;
	private final ServletContext servletContext;

	@Inject
	protected CDIWebContextFactory(final BeanManager beanManager, final ServletContext servletContext)
	{
		this.beanManager = beanManager;
		this.servletContext = servletContext;
	}

	/**
	 * Factory method for creating a {@link CDIWebContext} instance.
	 *
	 * @param request the servlet request object describing the current operation
	 * @param response the servlet response object describing the current operation
	 * @return the newly created {@link CDIWebContext} instance
	 */
	public IWebContext create(final HttpServletRequest request, final HttpServletResponse response)
	{
		return new CDIWebContext(request, response, beanManager, servletContext);
	}
}
