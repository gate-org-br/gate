package gate.adapter.metadata;

public class LocalDateMetadata extends SimpleMetadata
{
	public LocalDateMetadata()
	{
		super(builder()
				.description("Campos de data devem ser preenchidos no formato DD/MM/YYYY")
				.mask("##/##/####"));
	}
}
