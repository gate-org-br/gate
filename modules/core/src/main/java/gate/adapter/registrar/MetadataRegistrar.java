package gate.adapter.registrar;

import gate.adapter.metadata.Metadata;

import java.util.Map;

public interface MetadataRegistrar extends Registrar<Metadata>
{
	@Override
	Map<Class<?>, Metadata> entries();
}