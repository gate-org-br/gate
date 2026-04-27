package gate.lang.property.metadata;

public class LocalTimeMetadata extends SimpleMetadata
{
	public LocalTimeMetadata() {super(builder().description("Campos de hora devem ser preenchidos no formato HH:MM:SS").mask("##:##"));}
}
