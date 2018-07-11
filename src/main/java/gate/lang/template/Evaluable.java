package gate.lang.template;

import gate.error.EvaluableException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface Evaluable
{

	public void evaluate(List<Object> context,
			Map<String, Object> parameters,
			Writer writer) throws EvaluableException;
}
