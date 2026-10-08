package gate.sql.statement;

import java.util.List;

public interface Compilable
{

	SQL parameters(List<Object> parameters);

	SQL parameters(Object... parameters);

	SQL constant();
}
