package gate.lang.property.metadata;

public class HHMMSSDurationMetadata extends SimpleMetadata
{
	public HHMMSSDurationMetadata() {super(builder().description("Duração no formato HH:MM:SS").mask("##:##:##"));}
}
