package gate.lang.template;

import gate.error.EvaluableException;
import gate.lang.expression.Parameters;
import java.io.Writer;
import java.util.List;

public interface Evaluable
{

	void evaluate(Writer writer, List<Object> context, Parameters parameters) throws EvaluableException;
}
