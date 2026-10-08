package gate.adapter.converter;

import gate.type.mime.MimeData;

public class MimeDataConverterTest extends AbstractSimpleConverterTest<MimeData>
{
	@Override protected Class<MimeData> getType() {return MimeData.class;}

	@Override protected MimeData getValue() {return MimeData.of(new byte[]{1, 2, 3});}

	@Override protected String getString() {return "data:application/octet-stream;base64,AQID";}
}
