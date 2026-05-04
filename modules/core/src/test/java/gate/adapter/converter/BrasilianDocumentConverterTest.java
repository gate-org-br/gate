package gate.adapter.converter;

import gate.type.br.BrasilianDocument;

public class BrasilianDocumentConverterTest extends AbstractSimpleConverterTest<BrasilianDocument>
{
	@Override protected Class<BrasilianDocument> getType() {return BrasilianDocument.class;}
	@Override protected BrasilianDocument getValue() {return BrasilianDocument.valueOf("314.343.884-33");}
	@Override protected String getString() {return "314.343.884-33";}
}
