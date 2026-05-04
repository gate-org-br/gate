package gate.adapter.metadata;

public class CNPJMetadata extends SimpleMetadata
{
	public CNPJMetadata()
	{
		super(builder()
				.description("Campos de CNPJ devem ser preenchidos no formato 99.999.999/9999-99")
				.mask("##.###.###/####-##"));
	}
}
