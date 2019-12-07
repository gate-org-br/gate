package gate.lang.xml;

import gate.error.EvaluableException;
import java.io.Writer;

class VoidXMLEvaluable implements XMLEvaluable
{

	public static final VoidXMLEvaluable INSTANCE = new VoidXMLEvaluable();

	@Override
	public Type getType()
	{
		return Type.VOID;
	}

	@Override
	public void evaluate(Writer writer) throws EvaluableException
	{

	}
}
