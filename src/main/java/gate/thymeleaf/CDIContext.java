package gate.thymeleaf;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.LazyContextVariable;

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
				final Bean<?> bean = beanManager.resolve(beans);
				final CreationalContext<?> cctx = beanManager.createCreationalContext(bean);
				return beanManager.getReference(bean, Object.class, cctx);
			}
		};
	}
}
