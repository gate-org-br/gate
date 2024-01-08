package gate.thymeleaf;

import gate.Request;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

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
