package gate.adapter.converter;

import java.util.regex.Pattern;

public class PatternConverterTest extends AbstractSimpleConverterTest<Pattern>
{
	@Override protected Class<Pattern> getType() {return Pattern.class;}

	@Override protected Pattern getValue() {return Pattern.compile(getString());}

	@Override protected String getString() {return "[a-z]+";}
}
