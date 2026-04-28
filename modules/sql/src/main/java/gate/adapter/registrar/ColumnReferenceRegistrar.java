package gate.adapter.registrar;


import gate.sql.ColumnReference;

import java.util.Map;

public interface ColumnReferenceRegistrar extends Registrar<ColumnReference<?, ?>>
{
	Map<Class<?>, ColumnReference<?, ?>> entries();
}