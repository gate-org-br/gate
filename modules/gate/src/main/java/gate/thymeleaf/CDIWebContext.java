package gate.thymeleaf;

import gate.i18n.CurrentLocale;
import jakarta.enterprise.inject.spi.BeanManager;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.web.IWebExchange;

import java.util.Locale;

public class CDIWebContext extends CDIContext implements IWebContext
{

	private final IWebExchange webExchange;

	public CDIWebContext(Locale locale, IWebExchange webExchange, final BeanManager beanManager)
	{
		super(locale, beanManager);
		this.webExchange = webExchange;
	}

	public CDIWebContext(IWebExchange webExchange, final BeanManager beanManager)
	{
		this(CurrentLocale.get(), webExchange, beanManager);
	}

	@Override
	public IWebExchange getExchange()
	{
		return webExchange;
	}

}