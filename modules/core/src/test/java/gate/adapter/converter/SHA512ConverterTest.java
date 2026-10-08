package gate.adapter.converter;

import gate.type.SHA512;

public class SHA512ConverterTest extends AbstractSimpleConverterTest<SHA512>
{
	private static final String VALUE = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
	@Override protected Class<SHA512> getType() {return SHA512.class;}
	@Override protected SHA512 getValue() {return SHA512.valueOf(VALUE);}
	@Override protected String getString() {return VALUE;}
}
