package gate.thymeleaf;

import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.thymeleaf.context.LazyContextVariable;

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
			return createValue(request.getParameterMap()
				.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(),
					e -> e.getValue()[0])));

		if (values.containsKey(variable))
			return values.get(variable);

		if (request.getAttribute(variable) != null)
			return createValue(request.getAttribute(variable));

		Bean<?> bean = beanManager.resolve(beanManager.getBeans(variable));
		if (bean == null)
			return factory.createValueExpression(null, Object.class);
		CreationalContext<?> cctx = beanManager.createCreationalContext(bean);
		return createValue(beanManager.getReference(bean, Object.class, cctx));
	}

	private ValueExpression createValue(Object value)
	{
		if (value instanceof LazyContextVariable)
			value = ((LazyContextVariable) value).getValue();
		return factory.createValueExpression(value, Object.class);
	}

	@Override
	public ValueExpression setVariable(String variable, ValueExpression value)
	{
		return values.put(variable, value);
	}

}
