package gate.thymeleaf;

import java.util.Locale;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.thymeleaf.context.IContext;

/**
 * Factory bean for creating {@link CDIContext} instances.
 *
 * @author PÁLFALVI Tamás &lt;tamas.palfalvi@inbuss.hu&gt;
 */
@Dependent
public class CDIContextFactory
{

	private final BeanManager beanManager;

	@Inject
	protected CDIContextFactory(final BeanManager beanManager)
	{
		this.beanManager = beanManager;
	}

	/**
	 * Factory method for creating a {@link CDIContext} instance.
	 *
	 * @param locale the locale to be associated with the context; if <code>null</code>, the JVM's default locale will be used
	 *
	 * @return the newly created {@link CDIContext} instance
	 */
	public IContext create(final Locale locale)
	{
		return new CDIContext(locale == null ? Locale.getDefault() : locale, beanManager);
	}
}
