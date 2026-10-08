package gate.thymeleaf;

import jakarta.el.*;
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
		funcionMapper = new JaveELFunctionMapper(factory.getInitFunctionMap());
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