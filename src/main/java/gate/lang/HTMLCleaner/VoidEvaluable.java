package gate.lang.HTMLCleaner;

import gate.error.EvaluableException;
import java.io.Writer;

class VoidEvaluable implements Evaluable
{

	public static final VoidEvaluable INSTANCE = new VoidEvaluable();

	@Override
	public void evaluate(Writer writer) throws EvaluableException
	{

	}

}
