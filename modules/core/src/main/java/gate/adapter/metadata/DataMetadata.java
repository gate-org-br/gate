package gate.adapter.metadata;

public class DataMetadata extends SimpleMetadata
{
	public DataMetadata()
	{
		super(builder()
				.description("""
						Entre com um número de no máximo duas casas decimais seguido da unidade
						correspondente (B, K, M, G, T, P, E, Z ou Y)"""));
	}
}