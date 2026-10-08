package gate.adapter.metadata;

public class RangeMetadata extends SimpleMetadata
{
	public RangeMetadata()
	{
		super(builder()
				.description("Campos de intervalo de datas devem ser preenchidos no formato MIN - MAX or NUM"));
	}
}
