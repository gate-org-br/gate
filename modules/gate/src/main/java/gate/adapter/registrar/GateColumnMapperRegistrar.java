package gate.adapter.registrar;

import gate.adapter.columnMapper.AppColumnMapper;
import gate.adapter.columnMapper.ColumnMapper;
import gate.adapter.columnMapper.NamedTempFileColumnMapper;
import gate.adapter.columnMapper.TempFileColumnMapper;
import gate.entity.App;
import gate.type.NamedTempFile;
import gate.type.TempFile;

import java.util.HashMap;
import java.util.Map;

public class GateColumnMapperRegistrar implements ColumnMapperRegistrar
{
	@Override
	public Map<Class<?>, ColumnMapper> entries()
	{
		Map<Class<?>, ColumnMapper> registry = new HashMap<>();
		registry.put(App.class, new AppColumnMapper());
		registry.put(TempFile.class, new TempFileColumnMapper());
		registry.put(NamedTempFile.class, new NamedTempFileColumnMapper());
		return registry;
	}
}