package gate.lang.property.metadata;

public class EMailMetadata extends SimpleMetadata
{
	public EMailMetadata()
	{
		super(builder()
				.description("Campos de EMAILS devem conter endereços de E-Mail válidos"));
	}
}