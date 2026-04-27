package gate.lang.property.metadata;

public class YearMetadata extends SimpleMetadata
{
	public YearMetadata() {super(builder().description("Campos de mês/ano devem ser preenchidos no formato YYYY").mask("####"));}
}
