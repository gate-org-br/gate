package gate.thymeleaf;

import gate.Request;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

@ApplicationScoped
public class ELExpressionFactory
{

	@Inject
	BeanManager beanManager;

	public ELExpression create()
	{
		return new ELExpression.ELExpressionImpl(beanManager, Request.get());
	}

}
