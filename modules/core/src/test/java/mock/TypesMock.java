package mock;

import java.util.EnumMap;

@SuppressWarnings("unused")
public class TypesMock
{
	private TypesMock mock;
	private String name;
	private boolean bool;
	private char character;
	private byte byteValue;
	private short shortValue;
	private int integer;
	private long longValue;
	private float floatValue;
	private double doubleValue;
	private EnumMap<Option, String> results;

	public TypesMock getMock()
	{
		return mock == null ? mock = new TypesMock() : mock;
	}

	public void setMock(TypesMock mock)
	{
		this.mock = mock;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean getBool()
	{
		return bool;
	}

	public void setBool(boolean bool)
	{
		this.bool = bool;
	}

	public char getCharacter()
	{
		return character;
	}

	public void setCharacter(char character)
	{
		this.character = character;
	}

	public byte getByteValue()
	{
		return byteValue;
	}

	public void setByteValue(byte byteValue)
	{
		this.byteValue = byteValue;
	}

	public short getShortValue()
	{
		return shortValue;
	}

	public void setShortValue(short shortValue)
	{
		this.shortValue = shortValue;
	}

	public int getInteger()
	{
		return integer;
	}

	public void setInteger(int integer)
	{
		this.integer = integer;
	}

	public long getLongValue()
	{
		return longValue;
	}

	public void setLongValue(long longValue)
	{
		this.longValue = longValue;
	}

	public float getFloatValue()
	{
		return floatValue;
	}

	public void setFloatValue(float floatValue)
	{
		this.floatValue = floatValue;
	}

	public double getDoubleValue()
	{
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue)
	{
		this.doubleValue = doubleValue;
	}

	public EnumMap<Option, String> getResults()
	{
		return results == null ? results = new EnumMap<>(Option.class) : results;
	}

	public void setResults(EnumMap<Option, String> results)
	{
		this.results = results;
	}

	public enum Option
	{
		MALE, FEMALE
	}
}
