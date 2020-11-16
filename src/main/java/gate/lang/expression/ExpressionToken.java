package gate.lang.expression;

enum ExpressionToken
{
	EQ("Equals"),
	NE("Not Equals"),
	LT("Less Than"),
	GT("Greater Than"),
	GE("Not Less Than"),
	LE("Not Greater Than"),
	RX("Equals"),
	OPEN_PARENTHESES("Open Parentheses"),
	CLOSE_PARENTHESES("Close Parentheses"),
	NOT("Negation"),
	ADD("Adition"),
	SUB("Subtraction"),
	MUL("Multiplication"),
	DIV("Division"),
	MOD("Module"),
	AND("And"),
	OR("Or"),
	EOF("End"),
	EMPTY("Empty"),
	SIZE("Size"),
	DOT("Dot"),
	OPEN_BRACKET("Open Bracket"),
	CLOSE_BRACKET("Close Bracket"),
	COMMA("Comma"),
	VARIABLE("Variable"),
	CONTEXT("Context");

	private final String string;

	ExpressionToken(String string)
	{
		this.string = string;
	}

	@Override
	public String toString()
	{
		return string;
	}

}
