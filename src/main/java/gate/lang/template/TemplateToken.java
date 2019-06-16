package gate.lang.template;

import gate.error.TemplateException;
import gate.lang.expression.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

class TemplateToken implements Evaluable
{

	private final Type type;
	private final String value;
	private final Subtype sutbype;

	static final TemplateToken CONDITION = new TemplateToken(Type.CONDITION, "condition");
	static final TemplateToken EOF = new TemplateToken(Type.EOF, "");

	static final TemplateToken EQUALS = new TemplateToken(Type.EQUALS, "=");
	static final TemplateToken SOURCE = new TemplateToken(Type.SOURCE, "source");
	static final TemplateToken INDEX = new TemplateToken(Type.INDEX, "index");
	static final TemplateToken TARGET = new TemplateToken(Type.TARGET, "target");
	static final TemplateToken QUOTE = new TemplateToken(Type.QUOTE, "'");
	static final TemplateToken DOUBLE_QUOTE = new TemplateToken(Type.DOUBLE_QUOTE, "\"");

	static final TemplateToken EXPRESSION_HEAD = new TemplateToken(Type.EXPRESSION_HEAD, "${");

	static final TemplateToken EXPRESSION_TAIL = new TemplateToken(Type.EXPRESSION_TAIL, "}");

	static final TemplateToken IF_HEAD = new TemplateToken(Type.IF_HEAD, "<g-if");

	static final TemplateToken ITERATOR_HEAD = new TemplateToken(Type.ITERATOR_HEAD, "<g-iterator");

	static final TemplateToken CLOSE_TAG = new TemplateToken(Type.CLOSE_TAG, ">");

	static final TemplateToken IF_TAIL = new TemplateToken(Type.IF_TAIL, "</g-if>");

	static final TemplateToken ITERATOR_TAIL = new TemplateToken(Type.ITERATOR_TAIL, "</g-iterator>");

	public TemplateToken(Type type, String value)
	{
		this.type = type;
		this.value = value;
		this.sutbype = Subtype.INLINE;
	}

	public TemplateToken(Type type, Subtype subtype, String value)
	{
		this.type = type;
		this.value = value;
		this.sutbype = subtype;
	}

	public Type getType()
	{
		return type;
	}

	public Subtype getSutbype()
	{
		return sutbype;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public void evaluate(Writer writer, List<Object> context, Parameters parameters) throws TemplateException
	{
		try
		{
			writer.write(value);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate templete: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return String.format("%s: %s", type, value);
	}

	public enum Type
	{
		TEXT, EXPRESSION_HEAD, EXPRESSION_TAIL,
		IF_HEAD, IF_TAIL, ITERATOR_HEAD, ITERATOR_TAIL, SOURCE, TARGET, INDEX,
		CONDITION, QUOTE, DOUBLE_QUOTE, CLOSE_TAG, EQUALS, EOF
	}

	public enum Subtype
	{
		INLINE, LBLOCK, WBLOCK
	}
}
