package gate.adapter.converter;

import gate.type.Version;

public class VersionConverterTest extends AbstractSimpleConverterTest<Version>
{
	@Override protected Class<Version> getType() {return Version.class;}
	@Override protected Version getValue() {return Version.valueOf("1.2.3-RC-01");}
	@Override protected String getString() {return "1.2.3-RC-01";}
}
