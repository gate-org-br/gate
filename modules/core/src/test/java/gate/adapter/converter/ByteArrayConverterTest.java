package gate.adapter.converter;

public class ByteArrayConverterTest extends AbstractSimpleConverterTest<byte[]>
{
	@Override protected Class<byte[]> getType() {return byte[].class;}

	@Override protected byte[] getValue() {return new byte[]{1, 2, 3};}

	@Override protected String getString() {return "AQID";}
}
