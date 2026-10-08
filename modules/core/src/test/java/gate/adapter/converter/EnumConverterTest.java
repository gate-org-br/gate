package gate.adapter.converter;

public class EnumConverterTest extends AbstractSimpleConverterTest<EnumConverterTest.Option>
{
	enum Option {FIRST, SECOND}

	@Override protected Class<Option> getType() {return Option.class;}

	@Override protected Option getValue() {return Option.SECOND;}

	@Override protected String getString() {return "SECOND";}
}
