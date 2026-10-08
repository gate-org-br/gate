package gate.adapter.metadata;

public class SafeTextMetadata extends SimpleMetadata
{
	public SafeTextMetadata() {super(builder().description("Use apenas letras, números, espaços, quebras de linha e pontuação simples."));}
}
