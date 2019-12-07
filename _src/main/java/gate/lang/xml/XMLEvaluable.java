package gate.lang.xml;

import gate.error.EvaluableException;
import java.io.Writer;

interface XMLEvaluable
{

	Type getType();

	void evaluate(Writer writer) throws EvaluableException;

	enum Type
	{
		TOKEN, ENTITY, CHAR, VOID
	}
}
