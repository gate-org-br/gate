package gate.adapter.metadata;

public class LocalDateTimeIntervalMetadata extends SimpleMetadata
{
	public LocalDateTimeIntervalMetadata() {super(builder().description("Campos de intervalo de data/hora devem ser preenchidos no formato DD/MM/YYYY HH:MM - DD/MM/YYYY HH:MM").mask("##/##/#### ##:## - ##/##/#### ##:##"));}
}