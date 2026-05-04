package gate.adapter.converter;

public class ByteConverterTest extends AbstractSimpleConverterTest<Byte>
{
	@Override protected Class<Byte> getType() {return Byte.class;}

	@Override protected Byte getValue() {return Byte.valueOf(getString());}

	@Override protected String getString() {return "12";}
}
