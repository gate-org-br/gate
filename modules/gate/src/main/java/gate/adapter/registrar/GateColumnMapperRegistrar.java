package gate.adapter.registrar;

import gate.adapter.columnMapper.*;
import gate.entity.App;
import gate.lang.template.Template;
import gate.type.NamedTempFile;
import gate.type.TempFile;

import java.net.URI;
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
		registry.put(URI.class, new URIColumnMapper());
		registry.put(Template.class, new TemplateColumnMapper());
		return registry;
	}
}