package gate.lang.property.metadata;

public class RenavamMetadata extends SimpleMetadata
{
	public RenavamMetadata() {super(builder().description("Campos de Renavam devem ser preenchidos no formato 9999999999-9").mask("##########-#"));}
}
