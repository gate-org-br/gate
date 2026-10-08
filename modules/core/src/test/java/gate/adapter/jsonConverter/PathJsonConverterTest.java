package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.nio.file.Path;

public class PathJsonConverterTest extends AbstractSimpleJsonConverterTest<Path>
{
	@Override protected Class<Path> getType() {return Path.class;}
	@Override protected Path getValue() {return Path.of("/tmp/test");}
	@Override protected JsonElement getJson() {return JsonString.wrap("/tmp/test");}
}