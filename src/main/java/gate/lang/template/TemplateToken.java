package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

enum TemplateToken implements Evaluable
{
	EXPRESSION_HEAD("${"),
	EXPRESSION_TAIL("}"),
	IF_HEAD("<g:if"),
	IF_TAIL("</g:if>"),
	ITERATOR_HEAD("<g:iterator"),
	ITERATOR_TAIL("</g:iterator>"),
	SOURCE("source"),
	TARGET("target"),
	INDEX("index"),
	CONDITION("condition"),
	QUOTE("'"),
	DOUBLE_QUOTE("\""),
	CLOSE_TAG(">"),
	EQUALS("="),
	EOF(""),
	IMPORT("<g:import"),
	TYPE("type"),
	RESOURCE("resource"),
	DOT("."),
	SLASH("/"),
	OPEN_PARENTESIS("("),
	CLOSE_PARENTESIS(")"),
	SELF_CLOSE_TAG("/>"),
	SIMPLE_LINE_BREAK("\n"),
	COMPLEX_LINE_BREAK("\r\n"),
	SPACE(" "),
	TAB("\t");

	private final String string;

	TemplateToken(String string)
	{
		this.string = string;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			writer.write(string);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate templete: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return string;
	}
}
