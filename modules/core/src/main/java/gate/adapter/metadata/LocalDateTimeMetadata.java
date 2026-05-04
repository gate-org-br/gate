package gate.adapter.metadata;

public class LocalDateTimeMetadata extends SimpleMetadata
{
	public LocalDateTimeMetadata()
	{
		super(builder()
				.description("Campos de data/hora devem ser preenchidos no formato DD/MM/YYYY HH:MM")
				.mask("##/##/#### ##:##"));
	}
}
