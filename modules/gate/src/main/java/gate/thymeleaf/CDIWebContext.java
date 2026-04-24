package gate.thymeleaf;

import jakarta.enterprise.inject.spi.BeanManager;
import java.util.Locale;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.web.IWebExchange;

public class CDIWebContext extends CDIContext implements IWebContext
{

	private final IWebExchange webExchange;

	public CDIWebContext(Locale locale, IWebExchange webExchange, final BeanManager beanManager)
	{
		super(locale, beanManager);
		this.webExchange = webExchange;
	}

	@Override
	public IWebExchange getExchange()
	{
		return webExchange;
	}

}
