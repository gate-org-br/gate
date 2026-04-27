package gate.lang.property.metadata;

import gate.registrar.Registrar;

import java.util.Map;

public interface MetadataRegistrar extends Registrar<Metadata>
{
	@Override
	void register(Map<Class<?>, Metadata> registry);
}
