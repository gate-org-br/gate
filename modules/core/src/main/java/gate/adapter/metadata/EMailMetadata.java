package gate.adapter.metadata;

public class EMailMetadata extends SimpleMetadata
{
	public EMailMetadata()
	{
		super(builder()
				.description("Campos de EMAILS devem conter endereços de E-Mail válidos"));
	}
}