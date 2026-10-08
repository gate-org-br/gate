package gate.adapter.converter;

import gate.type.br.ProcessNumber;

public class ProcessNumberConverterTest extends AbstractSimpleConverterTest<ProcessNumber>
{
	@Override protected Class<ProcessNumber> getType() {return ProcessNumber.class;}
	@Override protected ProcessNumber getValue() {return ProcessNumber.valueOf("0000000000");}
	@Override protected String getString() {return "00.00.00000-0";}
}
