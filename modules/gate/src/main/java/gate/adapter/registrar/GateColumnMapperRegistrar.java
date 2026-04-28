package gate.adapter.registrar;

import gate.adapter.columnMapper.AppColumnMapper;
import gate.adapter.columnMapper.ColumnMapper;
import gate.entity.App;

import java.util.HashMap;
import java.util.Map;

public class GateColumnMapperRegistrar implements ColumnMapperRegistrar
{
	@Override
	public Map<Class<?>, ColumnMapper> entries()
	{
		Map<Class<?>, ColumnMapper> registry = new HashMap<>();
		registry.put(App.class, new AppColumnMapper());
		return registry;
	}
}