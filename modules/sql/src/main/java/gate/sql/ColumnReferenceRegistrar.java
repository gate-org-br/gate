package gate.sql;

import gate.registrar.Registrar;

import java.util.Map;

public interface ColumnReferenceRegistrar extends Registrar<ColumnReference<?, ?>>
{
	@Override void register(Map<Class<?>, ColumnReference<?, ?>> registry);
}