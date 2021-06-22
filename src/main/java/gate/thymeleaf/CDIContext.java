package gate.thymeleaf;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.LazyContextVariable;

/**
 * Thymeleaf context for non-web environments providing CDI named beans as expression root variables.
 *
 * The variables are enumerated eagerly upon context initialization, but their values are only queried upon use. This should be enough to prevent unnecessary
 * instantiation of beans that are available to, but not actually referenced in, the current view.
 *
 * @author PÁLFALVI Tamás &lt;tamas.palfalvi@inbuss.hu&gt;
 */
public class CDIContext implements IContext
{

	private final Locale locale;
	private final BeanManager beanManager;
	private final Set<String> names;

	protected CDIContext(final Locale locale, final BeanManager beanManager)
	{
		this.locale = locale;
		this.beanManager = beanManager;
		names = new HashSet<>();
		final Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>()
		{
		});
		beans.stream().map(b -> b.getName()).filter(name -> (name != null)).forEachOrdered(names::add);
	}

	@Override
	public Locale getLocale()
	{
		return locale;
	}

	@Override
	public boolean containsVariable(final String name)
	{
		return names.contains(name);
	}

	@Override
	public Set<String> getVariableNames()
	{
		return names;
	}

	@Override
	public Object getVariable(String name)
	{
		final Set<Bean<?>> beans = beanManager.getBeans(name);
		if (beans.isEmpty())
			return null;

		return new LazyContextVariable<Object>()
		{
			@Override
			protected Object loadValue()
			{
				try
				{
					final Bean<?> bean = beanManager.resolve(beans);
					final CreationalContext<?> cctx = beanManager.createCreationalContext(bean);
					return beanManager.getReference(bean, Object.class, cctx);
				} catch (final AmbiguousResolutionException are)
				{
					return null;
				}
			}
		};
	}
}
