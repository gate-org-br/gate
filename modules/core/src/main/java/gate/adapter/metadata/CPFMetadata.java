package gate.adapter.metadata;

public class CPFMetadata extends SimpleMetadata
{
	public CPFMetadata()
	{
		super(builder()
				.description("Campos de CPF devem ser preenchidos no formato 999.999.999-99")
				.mask("###.###.###-##"));
	}
}
