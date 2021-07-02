package gate.thymeleaf;

import java.util.Set;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;

public class GateDialect implements IDialect, IProcessorDialect
{

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
		return PRECEDENCE;
	}

	@Override
	public Set<IProcessor> getProcessors(String string)
	{
		return Set.of(new PrintAttributeTagProcessor(),
			new WriteAttributeTagProcessor(),
			new SelectTagProcessor(),
			new InputTagProcessor(),
			new TextAreaTagProcessor(),
			new RequestAttributeTagProcessor("module"),
			new RequestAttributeTagProcessor("screen"),
			new RequestAttributeTagProcessor("action"));
	}
}
