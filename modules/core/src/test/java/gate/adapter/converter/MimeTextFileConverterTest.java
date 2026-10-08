package gate.adapter.converter;

import gate.type.mime.MimeTextFile;

public class MimeTextFileConverterTest extends AbstractSimpleConverterTest<MimeTextFile>
{
	@Override protected Class<MimeTextFile> getType() {return MimeTextFile.class;}

	@Override protected MimeTextFile getValue() {return MimeTextFile.of("hello", "test.txt");}

	@Override protected String getString() {return "data:application/octet-stream;filename=test.txt;charset=UTF-8,hello";}
}
