package gate.adapter.metadata;

public class MACAddressMetadata extends SimpleMetadata
{
	public MACAddressMetadata()
	{
		super(builder()
				.description("Campos de endereço MAC devem ser preenchidos no formato HHHH.HHHH.HHHH")
				.mask("****.****.****"));
	}
}
