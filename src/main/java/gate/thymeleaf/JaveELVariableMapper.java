package gate.thymeleaf;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.http.HttpServletRequest;

public class JaveELVariableMapper extends VariableMapper
{

	private final ExpressionFactory factory;
	private final BeanManager beanManager;
	private final HttpServletRequest request;
	private final Map<String, ValueExpression> values = new HashMap<>();

	public JaveELVariableMapper(ExpressionFactory factory, BeanManager beanManager, HttpServletRequest request)
	{
		this.factory = factory;
		this.beanManager = beanManager;
		this.request = request;
	}

	@Override
	public ValueExpression resolveVariable(String variable)
	{
		if ("param".equals(variable))
			return factory.createValueExpression(request.getParameterMap()
				.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(),
					e -> e.getValue()[0])), Object.class);

		if (values.containsKey(variable))
			return values.get(variable);

		if (request.getAttribute(variable) != null)
			return factory.createValueExpression(request.getAttribute(variable), Object.class);

		Bean<?> bean = beanManager.resolve(beanManager.getBeans(variable));
		if (bean == null)
			return factory.createValueExpression(null, Object.class);
		CreationalContext<?> cctx = beanManager.createCreationalContext(bean);
		var value = beanManager.getReference(bean, Object.class, cctx);
		return factory.createValueExpression(value, Object.class);
	}

	@Override
	public ValueExpression setVariable(String variable, ValueExpression value)
	{
		return values.put(variable, value);
	}

}
