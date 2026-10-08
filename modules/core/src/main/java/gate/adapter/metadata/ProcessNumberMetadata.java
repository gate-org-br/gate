package gate.adapter.metadata;

public class ProcessNumberMetadata extends SimpleMetadata
{
	public ProcessNumberMetadata()
	{
		super(builder()
				.description("Campos de número de processo devem estar nos formatos 9999.99.99.999999-9 ou 99.99.99999-9 ou 9999999-99.9999.9.99.9999. Zeros à esquerda são obrigatórios. Caracteres de formatação são opcionais."));
	}
}
