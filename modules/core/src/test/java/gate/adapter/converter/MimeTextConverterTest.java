package gate.adapter.converter;

import gate.type.mime.MimeText;

public class MimeTextConverterTest extends AbstractSimpleConverterTest<MimeText>
{
	@Override protected Class<MimeText> getType() {return MimeText.class;}

	@Override protected MimeText getValue() {return MimeText.of("hello");}

	@Override protected String getString() {return "data:text/plain;charset=UTF-8,hello";}
}
