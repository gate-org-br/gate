package gate.thymeleaf;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class ELExpressionFactory
{

	@Inject
	BeanManager beanManager;

	@Inject
	HttpServletRequest request;

	public ELExpression create()
	{
		return new ELExpression.ELExpressionImpl(beanManager, request);
	}

}
