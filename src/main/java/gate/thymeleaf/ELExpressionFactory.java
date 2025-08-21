package gate.thymeleaf;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class ELExpressionFactory
{

	@Inject
	BeanManager beanManager;

	@Inject
	Provider<HttpServletRequest> request;

	@Produces
	@RequestScoped
	public ELExpression create()
	{
		return new ELExpression.ELExpressionImpl(beanManager, request.get());
	}

}
