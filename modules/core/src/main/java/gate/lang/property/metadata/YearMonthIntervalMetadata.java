package gate.lang.property.metadata;

public class YearMonthIntervalMetadata extends SimpleMetadata
{
	public YearMonthIntervalMetadata() {super(builder().description("Campos de intervalo de datas devem ser preenchidos no formato MM/YYYY - MM/YYYY").mask("##/#### - ##/####"));}
}
