package mock;

import java.util.Objects;

public final class DocumentMock
{
	private final String number;
	private final Type type;
	private final String issuer;

	public DocumentMock(String number, Type type, String issuer)
	{
		this.number = Objects.requireNonNull(number, "number is null");
		this.type = Objects.requireNonNull(type, "type is null");
		this.issuer = issuer;
	}

	@Deprecated
	public DocumentMock(String number)
	{
		this(number, Type.GENERIC, null);
	}

	public String getNumber()
	{
		return number;
	}

	public Type getType()
	{
		return type;
	}

	public String getIssuer()
	{
		return issuer;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DocumentMock document
			   && Objects.equals(number, document.number)
			   && type == document.type
			   && Objects.equals(issuer, document.issuer);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(number, type, issuer);
	}

	public enum Type
	{
		CPF, PASSPORT, GENERIC
	}
}
