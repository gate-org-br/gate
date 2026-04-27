package gate.registrar;

import gate.columnMapper.AppColumnMapper;
import gate.entity.App;
import gate.sql.columnMapper.ColumnMapper;
import gate.sql.columnMapper.ColumnMapperRegistrar;

import java.util.Map;

public class GateColumnMapperRegistrar implements ColumnMapperRegistrar
{

	@Override
	public void register(Map<Class<?>, ColumnMapper> registry)
	{
		registry.put(App.class, new AppColumnMapper());
	}
}