package gate.adapter.metadata;

public class CharacterMetadata extends SimpleMetadata
{
	public CharacterMetadata()
	{
		super(builder()
				.description("Campos de CARACTERE devem ser preenchidos com um único caractere."));
	}
}
