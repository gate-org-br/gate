package gate.lang.property.metadata;

public class YearMonthMetadata extends SimpleMetadata
{
	public YearMonthMetadata() {super(builder().description("Campos de mês/ano devem ser preenchidos no formato MM/YYYY").mask("##/####"));}
}
