package gate.adapter.converter;

public class StringConverterTest extends AbstractSimpleConverterTest<String>
{
	@Override protected Class<String> getType() {return String.class;}

	@Override protected String getValue() {return getString();}

	@Override protected String getString() {return "text";}
}
