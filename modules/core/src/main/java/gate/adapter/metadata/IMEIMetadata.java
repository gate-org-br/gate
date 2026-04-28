package gate.adapter.metadata;

public class IMEIMetadata extends SimpleMetadata
{
	public IMEIMetadata()
	{
		super(builder()
				.mask("##-######-######-#")
				.description("Campos de IMEI devem ser preenchidos no formato 99-999999-999999-9"));
	}
}