package gate.adapter.converter;

public class BooleanConverterTest extends AbstractSimpleConverterTest<Boolean>
{
	@Override protected Class<Boolean> getType() {return Boolean.class;}

	@Override protected Boolean getValue() {return Boolean.TRUE;}

	@Override protected String getString() {return "true";}
}
