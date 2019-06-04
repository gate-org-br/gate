package gate.lang.template;

import gate.error.EvaluableException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface Evaluable
{

	void evaluate(Writer writer, List<Object> context, Map<String, Object> parameters) throws EvaluableException;
}
