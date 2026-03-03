package gate.property;

import gate.entity.Role;
import gate.entity.User;
import gate.error.PropertyError;
import gate.lang.property.Property;
import gate.type.ID;
import gate.type.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest
{

	private Map<String, User> users;
	private Role role;

	public Map<String, User> getUsers()
	{
		return users;
	}

	@BeforeEach
	public void setUp()
	{
		role = new Role();
		role.setId(ID.valueOf(1));
		role.setName("Role 1");

		User user1 = new User();
		user1.setId(ID.valueOf(1));
		user1.setName("Usuário 1");
		role.getUsers().add(user1);

		User user2 = new User();
		user2.setId(ID.valueOf(2));
		user2.setName("Usuário 2");
		role.getUsers().add(user2);

		users = new HashMap<>();
		users.put("user1", user1);
		users.put("user2", user2);
	}

	@Test
	public void shouldSetNestedPropertyValue()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "mock.name");
		property.setValue(mock, "Mock");
		assertEquals("Mock", property.getValue(mock));
	}

	@Test
	public void shouldGetSimplePropertyValue()
	{
		assertEquals("Role 1", Property.getProperty(Role.class, "name").getValue(role));
	}

	@Test
	public void shouldThrowWhenPropertyPathStartsWithIndex()
	{
		assertThrows(PropertyError.class, () -> Property.getProperty(Role.class, "[0]"));
	}

	@Test
	public void shouldGetListElementNestedPropertyByIndex()
	{
		assertEquals("Usuário 1", Property.getProperty(Role.class, "users[0].name").getValue(role));
	}

	@Test
	public void shouldThrowWhenListIndexIsInvalidString()
	{
		assertThrows(PropertyError.class, () -> Property.getProperty(Role.class, "users['teste'].name"));
	}

	@Test
	public void shouldGetCollectionSizeThroughMethodCall()
	{
		assertEquals(2, Property.getProperty(Role.class, "users.size()").getValue(role));
	}

	@Test
	public void shouldGetMapNestedPropertyByDotKey()
	{
		assertEquals("Usuário 1", Property.getProperty(getClass(), "users.user1.name").getValue(this));
	}

	@Test
	public void shouldGetMapNestedPropertyByBracketKey()
	{
		String expected = "Usuário 1";
		Object result = Property.getProperty(getClass(), "users['user1'].name").getValue(this);
		assertEquals(expected, result);
	}

	@Test
	public void shouldInvokeMethodOnListElement()
	{
		Object result = Property
				.getProperty(Role.class, "users[0].checkAccess('module', 'screen', 'action')")
				.getValue(role);
		assertEquals(false, result);
	}

	@Test
	public void shouldSetPrimitivePropertyValue()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "integer");
		property.setValue(mock, 1);
		assertEquals(1, property.getValue(mock));
	}

	@Test
	public void shouldSetMapEntryByDotKey()
	{
		Property.getProperty(getClass(), "users.user3")
				.setValue(this, new User().setId(ID.valueOf(3)).setName("Usuário 3"));

		String expected = "Usuário 3";
		Object result = getUsers().get("user3").getName();
		assertEquals(expected, result);
	}

	@Test
	public void shouldSetBooleanPropertyValue()
	{
		Property.getProperty(Role.class, "active").setValue(role, true);
		assertEquals(Boolean.TRUE, role.getActive());
	}

	@Test
	public void shouldAppendElementUsingEmptyListIndex()
	{
		Property.getProperty(Role.class, "users[]")
				.setValue(role, new User().setId(ID.valueOf(3)).setName("Usuário 3"));
		assertEquals(1, role.getUsers().size());
	}

	@Test
	public void shouldRenderOriginalExpressionInToString()
	{
		assertEquals("users[1].name", Property.getProperty(Role.class, "users[1].name").toString());
	}

	@Test
	public void shouldGenerateColumnNameForNestedProperty()
	{
		assertEquals("Role$name", Property.getProperty(Role.class, "role.name").getColumnName());
	}

	@Test
	public void shouldSetBooleanPrimitivePropertyValue()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "bool");
		property.setValue(mock, true);
		assertEquals(true, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetBooleanAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "bool");
		property.setBoolean(mock, true);
		assertEquals(true, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetCharAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "character");
		property.setChar(mock, 'x');
		assertEquals('x', property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetByteAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "byteValue");
		property.setByte(mock, (byte) 3);
		assertEquals((byte) 3, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetShortAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "shortValue");
		property.setShort(mock, (short) 7);
		assertEquals((short) 7, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetIntAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "integer");
		property.setInt(mock, 11);
		assertEquals(11, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetLongAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "longValue");
		property.setLong(mock, 13L);
		assertEquals(13L, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetFloatAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "floatValue");
		property.setFloat(mock, 1.5f);
		assertEquals(1.5f, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetDoubleAccessorByType()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "doubleValue");
		property.setDouble(mock, 2.5d);
		assertEquals(2.5d, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetEnumMapValuesByBracketAndDotNotation()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "results[MALE]");
		property.setValue(mock, "MALE RESULT");
		assertEquals("MALE RESULT", property.getValue(mock));

		property = Property.getProperty(Mock.class, "results.FEMALE");
		property.setValue(mock, "FEMALE RESULT");
		assertEquals("FEMALE RESULT", property.getValue(mock));
	}


	@Test
	public void shouldGetAllDeclaredPropertiesFromClass()
	{
		assertEquals(11, Property.getProperties(Mock.class).size());
	}

	@Test
	public void shouldReadStaticValueByObjectAndName()
	{
		Mock mock = new Mock();
		mock.setName("A");
		assertEquals("A", Property.getValue(mock, "name"));
		assertNull(Property.getValue(null, "name"));
	}

	@Test
	public void shouldDetectWhenPropertyValueIsEmpty()
	{
		Mock mock = new Mock();
		Property property = Property.getProperty(Mock.class, "name");
		assertTrue(property.isEmpty(mock));
		property.setValue(mock, "X");
		assertFalse(property.isEmpty(mock));
	}

	@Test
	public void shouldGetPreviousPropertyFromNestedPath()
	{
		Property property = Property.getProperty(Mock.class, "mock.name");
		assertEquals("mock", property.getPreviousProperty().toString());
	}

	@Test
	public void shouldExposePropertyMetadataAndTypes()
	{
		Property property = Property.getProperty(Mock.class, "results[MALE]");
		assertEquals(Mock.class, property.getOwner());
		assertEquals(String.class, property.getRawType());
		assertEquals(Object.class, property.getElementRawType());
		assertEquals(Object.class, property.getElementType());
		assertEquals(String.class, property.getType());
		assertNull(property.getColor());
		assertNull(property.getIcon());
		assertNull(property.getDescription());
		assertNull(property.getTooltip());
		assertNull(property.getPlaceholder());
		assertNull(property.getMask());
		assertEquals(property.getAttributes().get(property.getAttributes().size() - 1), property.getLastAttribute());
	}

	@Test
	public void shouldDocumentCurrentBehaviorOfPrimitiveGetters()
	{
		Mock mock = new Mock();
		mock.setBool(true);
		mock.setCharacter('z');
		mock.setByteValue((byte) 1);
		mock.setShortValue((short) 2);
		mock.setInteger(3);
		mock.setLongValue(4L);
		mock.setFloatValue(5.0f);
		mock.setDoubleValue(6.0d);

		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "bool").getBoolean(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "character").getChar(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "byteValue").getByte(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "shortValue").getShort(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "integer").getInt(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "longValue").getLong(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "floatValue").getFloat(mock));
		assertThrows(IllegalStateException.class, () -> Property.getProperty(Mock.class, "doubleValue").getDouble(mock));
	}

	@SuppressWarnings("unused")
	static class Mock
	{

		public Mock()
		{
		}

		private Mock mock;
		private String name;
		private boolean bool;
		private char character;
		private byte byteValue;
		private short shortValue;
		private int integer;
		private long longValue;
		private float floatValue;
		private double doubleValue;
		private EnumMap<Sex, String> results;

		public Mock getMock()
		{
			return mock;
		}

		public void setMock(Mock mock)
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

		public EnumMap<Sex, String> getResults()
		{
			if (results == null)
				results = new EnumMap<>(Sex.class);
			return results;
		}

		public void setResults(EnumMap<Sex, String> results)
		{
			this.results = results;
		}
	}
}
