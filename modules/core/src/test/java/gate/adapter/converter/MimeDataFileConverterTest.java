package gate.adapter.converter;

import gate.lang.contentType.ContentType;
import gate.type.mime.MimeDataFile;

public class MimeDataFileConverterTest extends AbstractSimpleConverterTest<MimeDataFile>
{
	@Override protected Class<MimeDataFile> getType() {return MimeDataFile.class;}

	@Override protected MimeDataFile getValue()
	{
		return MimeDataFile.of(ContentType.of("application", "octet-stream"), new byte[]{1, 2, 3}, "test.bin");
	}

	@Override protected String getString() {return "data:application/octet-stream;filename=test.bin;base64,AQID";}
}
