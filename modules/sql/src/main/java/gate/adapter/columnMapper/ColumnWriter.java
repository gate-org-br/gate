package gate.adapter.columnMapper;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;

public interface ColumnWriter
{

	PreparedStatement getPreparedStatement();

	ColumnWriter onClose(Runnable onClose);
}
