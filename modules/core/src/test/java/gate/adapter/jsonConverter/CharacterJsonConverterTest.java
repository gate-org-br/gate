package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

public class CharacterJsonConverterTest extends AbstractSimpleJsonConverterTest<Character>
{
	@Override protected Class<Character> getType() {return Character.class;}
	@Override protected Character getValue() {return 'A';}
	@Override protected JsonElement getJson() {return JsonString.of("A");}
}
