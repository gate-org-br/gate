package gate.adapter.converter;

import gate.type.Result;

public class ResultConverterTest extends AbstractSimpleConverterTest<Result>
{
	@Override protected Class<Result> getType() {return Result.class;}

	@Override protected Result getValue() {return Result.success("ok");}

	@Override protected String getString() {return "{\"type\":\"SUCCESS\",\"message\":\"ok\",\"data\":null}";}
}
