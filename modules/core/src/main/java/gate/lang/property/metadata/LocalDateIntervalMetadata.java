package gate.lang.property.metadata;

public class LocalDateIntervalMetadata extends SimpleMetadata
{
	public LocalDateIntervalMetadata() {super(builder().description("Campos de intervalo de datas devem ser preenchidos no formato DD/MM/YYYY - DD/MM/YYYY").mask("##/##/#### - ##/##/####"));}
}
