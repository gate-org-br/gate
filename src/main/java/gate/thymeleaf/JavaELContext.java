package gate.thymeleaf;

import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.FunctionMapper;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.ResourceBundleELResolver;
import jakarta.el.StaticFieldELResolver;
import jakarta.el.VariableMapper;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.http.HttpServletRequest;

public class JavaELContext extends ELContext
{

	private final CompositeELResolver resolver;
	private final FunctionMapper funcionMapper;
	private final VariableMapper variableMapper;

	public JavaELContext(ExpressionFactory factory,
		BeanManager beanManager,
		HttpServletRequest request)
	{
		resolver = new CompositeELResolver();
		resolver.add(factory.getStreamELResolver());
		resolver.add(new StaticFieldELResolver());
		resolver.add(new MapELResolver());
		resolver.add(new ResourceBundleELResolver());
		resolver.add(new ListELResolver());
		resolver.add(new ArrayELResolver());
		resolver.add(new BeanELResolver());
		funcionMapper = new JaveELFuncionMapper(factory.getInitFunctionMap());
		variableMapper = new JaveELVariableMapper(factory, beanManager, request);
	}

	@Override
	public ELResolver getELResolver()
	{
		return resolver;
	}

	@Override
	public FunctionMapper getFunctionMapper()
	{
		return funcionMapper;
	}

	@Override
	public VariableMapper getVariableMapper()
	{
		return variableMapper;
	}
}
