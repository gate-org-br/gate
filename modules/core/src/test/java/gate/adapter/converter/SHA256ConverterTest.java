package gate.adapter.converter;

import gate.type.SHA256;

public class SHA256ConverterTest extends AbstractSimpleConverterTest<SHA256>
{
	private static final String VALUE = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
	@Override protected Class<SHA256> getType() {return SHA256.class;}
	@Override protected SHA256 getValue() {return SHA256.valueOf(VALUE);}
	@Override protected String getString() {return VALUE;}
}
