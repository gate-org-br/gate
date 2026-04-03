package gate.sql.condition;

import gate.entity.User;
import gate.lang.property.Property;
import gate.type.PropertyReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertyConditionTest
{

    @Test
    public void testPropertyPredicateAndRollbackFlow()
    {
        Property id = Property.getProperty(User.class, "id");
        Property name = Property.getProperty(User.class, "name");
        Property roleId = Property.getProperty(User.class, "role.id");

        PropertyCondition condition = Condition.of("column1").isEq(id)
                .and().when(true).expression("column2").isNe(name)
                .and().when(false).expression("column3").isGt(roleId)
                .or().not("column4").isLt(name);

        assertEquals("column1 = ? and column2 <> ? or not column4 < ?", condition.toString());
        assertTrue(condition.getParameters().toList().isEmpty());
        assertEquals(List.of(id, name, name), condition.getProperties().toList());
    }

    @Test
    public void testPropertyConditionAndOrComposition()
    {
        Property id = Property.getProperty(User.class, "id");
        Property name = Property.getProperty(User.class, "name");

        PropertyCondition left = Condition.of("left_col").isEq(id);
        PropertyCondition right = Condition.of("right_col").isEq(name);

        Condition condition = left
                .and(right)
                .or(Condition.of("const_col").isEq("const_value"));

        assertEquals("left_col = ? and (right_col = ?) or (const_col = const_value)",
                condition.toString());
        assertTrue(condition.getParameters().toList().isEmpty());
        assertEquals(List.of(id, name), condition.getProperties().toList());
    }

    @Test
    public void testPropertyConstants()
    {
        assertEquals("0 = 0", PropertyCondition.PROPERTY_TRUE.toString());
        assertEquals("0 = 1", PropertyCondition.PROPERTY_FALSE.toString());
    }

    @Test
    public void testPropertyReferenceAndOrChain()
    {
        Property id = Property.getProperty(User.class, "id");
        Property active = Property.getProperty(User.class, "active");
        Property roleId = Property.getProperty(User.class, "role.id");

        PropertyCondition condition = Condition.of("name").isEq(id)
                .and(User::getActive).isEq(active)
                .or(User::getRole).isEq(roleId);

        assertEquals("name = ? and active = ? or Role$id = ?", condition.toString());
        assertEquals(List.of(id, active, roleId), condition.getProperties().toList());
    }

    @Test
    public void testPropertyRollbackSkipsPropertyReferenceResolution()
    {
        Property id = Property.getProperty(User.class, "id");
        PropertyReference<User, String> invalidReference = user -> user.getName();

        PropertyCondition condition = Condition.of("column1").isEq(id)
                .and().when(false).expression(invalidReference).isEq(id)
                .or().when(false).not(invalidReference).isEq(id);

        assertEquals("column1 = ?", condition.toString());
        assertEquals(List.of(id), condition.getProperties().toList());
    }
}
