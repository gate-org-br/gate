package gate.thymeleaf;

import gate.thymeleaf.processors.Processor;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;

@ApplicationScoped
public class GateDialect implements IDialect, IProcessorDialect
{

	@Inject
	Instance<Processor> processors;

	@Override
	public String getName()
	{
		return "Gate";
	}

	@Override
	public String getPrefix()
	{
		return "g";
	}

	@Override
	public int getDialectProcessorPrecedence()
	{
		return Precedence.DEFAULT;
	}

	@Override
	public Set<IProcessor> getProcessors(String string)
	{
		return processors.stream().collect(Collectors.toSet());
	}

}
