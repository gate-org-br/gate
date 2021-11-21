package gate.thymeleaf;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.StaticFieldELResolver;
import javax.el.VariableMapper;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.http.HttpServletRequest;

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
