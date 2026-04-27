package gate.lang.property.metadata;

public class LocalTimeIntervalMetadata extends SimpleMetadata
{
	public LocalTimeIntervalMetadata() {super(builder().description("Campos de intervalo de hora devem ser preenchidos no formato HH:MM - HH:MM").mask("##:## - ##:##"));}
}
