package gate.lang.property;

import mock.UserMock;
import mock.RoleMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FieldAttributeTest
{
	@Test
	void shouldReadNullSafeGetterThroughBackingField() throws NoSuchFieldException
	{
		var user = new UserMock();
		var field = UserMock.class.getDeclaredField("role");
		var attribute = FieldAttribute.of(field);

		assertNull(attribute.getValue(user));
		assertNull(attribute.getFieldValue(user));
	}

	@Test
	void shouldSetNullSafeAttributeWithoutTriggeringGetterInstantiation()
	{
		var user = new UserMock();
		var role = new RoleMock();
		var attribute = FieldAttribute.getAttributes(UserMock.class).get("role");

		attribute.setValue(user, role);

		assertSame(role, attribute.getValue(user));
		assertSame(role, attribute.getFieldValue(user));
	}
}
