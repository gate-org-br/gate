package gate.lang.HTMLCleaner;

import gate.error.EvaluableException;
import java.io.Writer;

interface Evaluable
{

	void evaluate(Writer writer) throws EvaluableException;
}
