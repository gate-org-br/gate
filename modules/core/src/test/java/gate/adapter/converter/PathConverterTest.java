package gate.adapter.converter;

import java.nio.file.Path;

public class PathConverterTest extends AbstractSimpleConverterTest<Path>
{
	@Override protected Class<Path> getType() {return Path.class;}

	@Override protected Path getValue() {return Path.of(getString());}

	@Override protected String getString() {return "tmp/test.txt";}
}
