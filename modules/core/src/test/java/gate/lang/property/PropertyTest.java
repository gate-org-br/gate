package gate.lang.property;

import gate.error.PropertyError;
import mock.MockFactory;
import mock.RoleMock;
import mock.TypesMock;
import mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest
{

	private Map<String, UserMock> users;
	private RoleMock role;

	public Map<String, UserMock> getUsers()
	{
		return users;
	}

	@BeforeEach
	public void setUp()
	{
		role = MockFactory.role();
		role.getUsers().add(MockFactory.user(1));
		role.getUsers().add(MockFactory.user(2));

		users = new HashMap<>();
		users.put("user1", role.getUsers().get(0));
		users.put("user2", role.getUsers().get(1));
	}

	@Test
	public void shouldSetNestedPropertyValue()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "mock.name");
		property.setValue(mock, "Mock");
		assertEquals("Mock", property.getValue(mock));
	}

	@Test
	public void shouldGetSimplePropertyValue()
	{
		assertEquals("Role", Property.getProperty(RoleMock.class, "name").getValue(role));
	}

	@Test
	public void shouldThrowWhenPropertyPathStartsWithIndex()
	{
		assertThrows(PropertyError.class, () -> Property.getProperty(RoleMock.class, "[0]"));
	}

	@Test
	public void shouldGetListElementNestedPropertyByIndex()
	{
		assertEquals("User 1", Property.getProperty(RoleMock.class, "users[0].name").getValue(role));
	}

	@Test
	public void shouldThrowWhenListIndexIsInvalidString()
	{
		assertThrows(PropertyError.class, () -> Property.getProperty(RoleMock.class, "users['teste'].name"));
	}

	@Test
	public void shouldGetCollectionSizeThroughMethodCall()
	{
		assertEquals(2, Property.getProperty(RoleMock.class, "users.size()").getValue(role));
	}

	@Test
	public void shouldGetMapNestedPropertyByDotKey()
	{
		assertEquals("User 1", Property.getProperty(getClass(), "users.user1.name").getValue(this));
	}

	@Test
	public void shouldGetMapNestedPropertyByBracketKey()
	{
		String expected = "User 1";
		Object result = Property.getProperty(getClass(), "users['user1'].name").getValue(this);
		assertEquals(expected, result);
	}

	@Test
	public void shouldInvokeMethodOnListElement()
	{
		Object result = Property
				.getProperty(RoleMock.class, "users[0].checkAccess('module', 'screen', 'action')")
				.getValue(role);
		assertEquals(false, result);
	}

	@Test
	public void shouldSetPrimitivePropertyValue()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "integer");
		property.setValue(mock, 1);
		assertEquals(1, property.getValue(mock));
	}

	@Test
	public void shouldSetMapEntryByDotKey()
	{
		Property.getProperty(getClass(), "users.user3")
				.setValue(this, MockFactory.user(3));

		String expected = "User 3";
		Object result = getUsers().get("user3").getName();
		assertEquals(expected, result);
	}

	@Test
	public void shouldSetBooleanPropertyValue()
	{
		Property.getProperty(RoleMock.class, "active").setValue(role, true);
		assertEquals(Boolean.TRUE, role.getActive());
	}

	@Test
	public void shouldAppendElementUsingEmptyListIndex()
	{
		Property.getProperty(RoleMock.class, "users[]")
				.setValue(role, MockFactory.user(3));
		assertEquals(1, role.getUsers().size());
	}

	@Test
	public void shouldRenderOriginalExpressionInToString()
	{
		assertEquals("users[1].name", Property.getProperty(RoleMock.class, "users[1].name").toString());
	}

	@Test
	public void shouldSetBooleanPrimitivePropertyValue()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "bool");
		property.setValue(mock, true);
		assertEquals(true, property.getValue(mock));
	}

	@Test
	public void shouldSetDeepNestedPropertyValue()
	{
		UserMock user = MockFactory.user();
		Property property = Property.getProperty(UserMock.class, "role.manager.role.manager.role.manager.id");
		property.setValue(user, 1);
		assertEquals(1, property.getValue(user));
	}

	@Test
	public void shouldSetAndGetBooleanAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "bool");
		property.setBoolean(mock, true);
		assertEquals(true, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetCharAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "character");
		property.setChar(mock, 'x');
		assertEquals('x', property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetByteAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "byteValue");
		property.setByte(mock, (byte) 3);
		assertEquals((byte) 3, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetShortAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "shortValue");
		property.setShort(mock, (short) 7);
		assertEquals((short) 7, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetIntAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "integer");
		property.setInt(mock, 11);
		assertEquals(11, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetLongAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "longValue");
		property.setLong(mock, 13L);
		assertEquals(13L, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetFloatAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "floatValue");
		property.setFloat(mock, 1.5f);
		assertEquals(1.5f, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetDoubleAccessorByType()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "doubleValue");
		property.setDouble(mock, 2.5d);
		assertEquals(2.5d, property.getValue(mock));
	}

	@Test
	public void shouldSetAndGetEnumMapValuesByBracketAndDotNotation()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "results[MALE]");
		property.setValue(mock, "MALE RESULT");
		assertEquals("MALE RESULT", property.getValue(mock));

		property = Property.getProperty(TypesMock.class, "results.FEMALE");
		property.setValue(mock, "FEMALE RESULT");
		assertEquals("FEMALE RESULT", property.getValue(mock));
	}


	@Test
	public void shouldGetAllDeclaredPropertiesFromClass()
	{
		assertEquals(11, Property.getProperties(TypesMock.class).size());
	}

	@Test
	public void shouldReadStaticValueByObjectAndName()
	{
		TypesMock mock = new TypesMock();
		mock.setName("A");
		assertEquals("A", Property.getValue(mock, "name"));
		assertNull(Property.getValue(null, "name"));
	}

	@Test
	public void shouldDetectWhenPropertyValueIsEmpty()
	{
		TypesMock mock = new TypesMock();
		Property property = Property.getProperty(TypesMock.class, "name");
		assertTrue(property.isEmpty(mock));
		property.setValue(mock, "X");
		assertFalse(property.isEmpty(mock));
	}

	@Test
	public void shouldGetPreviousPropertyFromNestedPath()
	{
		Property property = Property.getProperty(TypesMock.class, "mock.name");
		assertEquals("mock", property.getPreviousProperty().toString());
	}

	@Test
	public void shouldExposePropertyMetadataAndTypes()
	{
		Property property = Property.getProperty(TypesMock.class, "results[MALE]");
		assertEquals(TypesMock.class, property.getOwner());
		assertEquals(String.class, property.getRawType());
		assertEquals(Object.class, property.getElementRawType());
		assertEquals(Object.class, property.getElementType());
		assertEquals(String.class, property.getType());
		assertNull(property.getMetadata().color());
		assertNull(property.getMetadata().icon());
		assertNull(property.getMetadata().description());
		assertNull(property.getMetadata().tooltip());
		assertNull(property.getMetadata().placeholder());
		assertNull(property.getMetadata().mask());
		assertEquals(property.getAttributes().get(property.getAttributes().size() - 1), property.getLastAttribute());
	}

	@Test
	public void shouldDocumentCurrentBehaviorOfPrimitiveGetters()
	{
		TypesMock mock = new TypesMock();
		mock.setBool(true);
		mock.setCharacter('z');
		mock.setByteValue((byte) 1);
		mock.setShortValue((short) 2);
		mock.setInteger(3);
		mock.setLongValue(4L);
		mock.setFloatValue(5.0f);
		mock.setDoubleValue(6.0d);

		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "bool").getBoolean(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "character").getChar(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "byteValue").getByte(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "shortValue").getShort(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "integer").getInt(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "longValue").getLong(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "floatValue").getFloat(mock));
		assertThrows(Throwable.class, () -> Property.getProperty(TypesMock.class, "doubleValue").getDouble(mock));
	}
}