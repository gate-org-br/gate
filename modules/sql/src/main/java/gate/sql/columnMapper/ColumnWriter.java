package gate.sql.columnMapper;

import java.sql.PreparedStatement;

public interface ColumnWriter
{

	PreparedStatement getPreparedStatement();

	ColumnWriter onClose(Runnable onClose);
}
