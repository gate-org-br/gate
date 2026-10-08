package gate.adapter.converter;

public class CharacterConverterTest extends AbstractSimpleConverterTest<Character>
{
	@Override protected Class<Character> getType() {return Character.class;}

	@Override protected Character getValue() {return getString().charAt(0);}

	@Override protected String getString() {return "A";}
}
