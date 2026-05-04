package gate.adapter.converter;

import gate.lang.expression.Expression;

public class ExpressionConverterTest extends AbstractSimpleConverterTest<Expression>
{
	@Override protected Class<Expression> getType() {return Expression.class;}

	@Override protected Expression getValue() {return Expression.valueOf(getString());}

	@Override protected String getString() {return "x > 0";}
}