package gate.adapter.metadata;

public class CEPMetadata extends SimpleMetadata
{
	public CEPMetadata() {super(builder().description("Campos de CEP devem ser preenchidos no formato 99999-999").mask("##.###-###"));}
}