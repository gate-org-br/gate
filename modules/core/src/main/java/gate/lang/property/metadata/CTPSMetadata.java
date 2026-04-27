package gate.lang.property.metadata;

public class CTPSMetadata extends SimpleMetadata
{
	public CTPSMetadata() {super(builder().description("Campos de CTPS devem ser preenchidos no formato NNNNN SSSSS-UF").mask("##### #####-__"));}
}
