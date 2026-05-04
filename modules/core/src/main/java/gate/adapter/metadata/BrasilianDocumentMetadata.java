package gate.adapter.metadata;

public class BrasilianDocumentMetadata extends SimpleMetadata
{
	public BrasilianDocumentMetadata()
	{
		super(builder()
				.description("CPF ou CNPJ"));
	}
}
