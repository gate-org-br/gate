package gate.lang.property.metadata;

import gate.adapter.metadata.SimpleMetadata;

public class SafeNameMetadata extends SimpleMetadata
{
	public SafeNameMetadata() {super(builder().description("Use apenas letras, números, espaços ou hífen."));}
}