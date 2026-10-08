package gate.adapter.metadata;

public class VersionMetadata extends SimpleMetadata
{
	public VersionMetadata()
	{
		super(builder()
				.description("Número de versão padrão maven"));
	}
}
