package gate.sql.condition;

import gate.entity.User;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.GQN;
import gate.sql.select.Select;
import gate.type.ID;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompiledConditionTest
{

    @Test
    public void testEq()
    {
        Condition condition = Condition.of("column1").eq(1)
                .and().not("column2").eq(2)
                .or().when(true).expression("column3").eq(() -> 3)
                .and().when(true).not("column4").eq(() -> 4)
                .and().when(false).expression("column3").eq(() -> 5)
                .and().when(false).not("column4").eq(() -> 6)
                .or("column7").eq(7)
                .and("column8").eq(null)
                .and().not("column9").eq(null);
        assertEquals("column1 = ? and not column2 = ? or column3 = ? and not column4 = ? or column7 = ?", condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testNe()
    {
        Condition condition = Condition.of("column1").ne(1)
                .and().not("column2").ne(2)
                .or().when(true)
                .expression("column3").ne(() -> 3)
                .and().when(true).not("column4").ne(() -> 4)
                .and().when(false).expression("column3").ne(() -> 5)
                .and().when(false).not("column4").ne(() -> 6)
                .or("column7").ne(7)
                .and("column8").ne(null)
                .and().not("column9").ne(null);
        assertEquals(
                "column1 <> ? and not column2 <> ? or column3 <> ? and not column4 <> ? or column7 <> ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testLt()
    {
        Condition condition = Condition.of("column1").lt(1)
                .and().not("column2").lt(2)
                .or()
                .when(true).expression("column3").lt(() -> 3)
                .and().when(true).not("column4").lt(() -> 4)
                .and()
                .when(false).expression("column3").lt(() -> 5)
                .and().when(false).not("column4").lt(() -> 6)
                .or("column7").lt(7)
                .and("column8").lt(null)
                .and().not("column9").lt(null);
        assertEquals(
                "column1 < ? and not column2 < ? or column3 < ? and not column4 < ? or column7 < ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testLe()
    {
        Condition condition = Condition
                .of("column1").le(1)
                .and().not("column2").le(2)
                .or().when(true).expression("column3").le(() -> 3)
                .and().when(true).not("column4").le(() -> 4)
                .and().when(false).expression("column3").le(() -> 5)
                .and().when(false).not("column4").le(() -> 6)
                .or("column7").le(7)
                .and("column8").le(null)
                .and().not("column9").le(null);
        assertEquals(
                "column1 <= ? and not column2 <= ? or column3 <= ? and not column4 <= ? or column7 <= ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testGt()
    {
        Condition condition = Condition.of("column1").gt(1)
                .and().not("column2").gt(2)
                .or()
                .when(true).expression("column3").gt(() -> 3)
                .and().when(true).not("column4").gt(() -> 4)
                .and()
                .when(false).expression("column3").gt(() -> 5)
                .and().when(false).not("column4").gt(() -> 6)
                .or("column7").gt(7)
                .and("column8").gt(null)
                .and().not("column9").gt(null);
        assertEquals(
                "column1 > ? and not column2 > ? or column3 > ? and not column4 > ? or column7 > ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testGe()
    {
        Condition condition = Condition.of("column1").ge(1)
                .and().not("column2").ge(2)
                .or()
                .when(true).expression("column3").ge(() -> 3)
                .and().when(true).not("column4").ge(() -> 4)
                .and()
                .when(false).expression("column3").ge(() -> 5)
                .and().when(false).not("column4").ge(() -> 6)
                .or("column7").ge(7)
                .and("column8").ge(null)
                .and().not("column9").ge(null);
        assertEquals(
                "column1 >= ? and not column2 >= ? or column3 >= ? and not column4 >= ? or column7 >= ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testLk()
    {
        Condition condition = Condition.of("column1").lk(1)
                .and().not("column2").lk(2)
                .or()
                .when(true).expression("column3").lk(() -> 3)
                .and().when(true).not("column4").lk(() -> 4)
                .and().when(false).expression("column3").lk(() -> 5)
                .and().when(false).not("column4").lk(() -> 6)
                .or("column7").lk(7)
                .and("column8").lk(null)
                .and().not("column9").lk(null);
        assertEquals(
                "column1 like ? and not column2 like ? or column3 like ? and not column4 like ? or column7 like ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of("%1%", "%2%", "%3%", "%4%", "%7%"));
    }

    @Test
    public void testRx()
    {
        Condition condition = Condition.of("column1").rx(1)
                .and().not("column2").rx(2)
                .or()
                .when(true).expression("column3").rx(() -> 3)
                .and().when(true).not("column4").rx(() -> 4)
                .and()
                .when(false).expression("column3").rx(() -> 5)
                .and().when(false).not("column4").rx(() -> 6)
                .or("column7").rx(7)
                .and("column8").rx(null)
                .and().not("column9").rx(null);
        assertEquals(
                "column1 rlike ? and not column2 rlike ? or column3 rlike ? and not column4 rlike ? or column7 rlike ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of("1", "2", "3", "4", "7"));
    }

    @Test
    public void testBw()
    {
        Condition condition = Condition.of("column1").bw(1)
                .and().not("column2").bw(2)
                .or()
                .when(true).expression("column3").bw(() -> 3)
                .and().when(true).not("column4").bw(() -> 4)
                .and().when(false).expression("column3").bw(() -> 5)
                .and().when(false).not("column4").bw(() -> 6)
                .or("column7").bw(7)
                .and("column8").bw(null)
                .and().not("column9").bw(null);
        assertEquals(
                "column1 between ? and ? and not column2 between ? and ? or column3 between ? and ? and not column4 between ? and ? or column7 between ? and ?",
                condition.toString());
        assertEquals(condition.getParameters().toList(), List.of(1, 2, 3, 4, 7));
    }

    @Test
    public void testAndChainSkipsNullPredicate()
    {
        CompiledCondition condition =
                Condition.of("id").eq(1)
                        .and("name").eq("Person 1")
                        .and("birthdate").gt(null);

        List<Object> parameters = condition.getParameters().toList();
        assertEquals("id = ? and name = ?", condition.toString());
        assertEquals(2, parameters.size());
        assertEquals(1, parameters.get(0));
        assertEquals("Person 1", parameters.get(1));
    }

    @Test
    public void testOrChainSkipsNullPredicate()
    {
        CompiledCondition condition =
                Condition.of("id").eq(1).or("name").eq("Person 1").or("birthdate").gt(null);

        List<Object> parameters = condition.getParameters().toList();
        assertEquals("id = ? or name = ?", condition.toString());
        assertEquals(2, parameters.size());
        assertEquals(1, parameters.get(0));
        assertEquals("Person 1", parameters.get(1));
    }

    @Test
    public void testAndPropertyEquals()
    {
        Condition condition = Condition
                .of(Entity.getFullColumnName(Property.getProperty(User.class, "id")))
                .eq(ID.valueOf(1))
                .and(Entity.getFullColumnName(Property.getProperty(User.class, "name")))
                .eq("Person 1")
                .and(Entity.getFullColumnName(Property.getProperty(User.class, "role.id")))
                .eq(ID.valueOf(2))
                .and(Entity.getFullColumnName(Property.getProperty(User.class, "role.name")))
                .eq(null);
        assertEquals("Uzer.id = ? and Uzer.name = ? and Uzer$Role.id = ?", condition.toString());
        assertEquals(condition.getParameters().toList(),
                Arrays.asList(ID.valueOf(1), "Person 1", ID.valueOf(2)));
    }

    @Test
    public void testOrPropertyEquals()
    {
        CompiledCondition condition =
                Condition.of(Entity.getFullColumnName(Property.getProperty(User.class, "id")))
                        .eq(ID.valueOf(1))
                        .or(Entity.getFullColumnName(Property.getProperty(User.class, "name")))
                        .eq("Person 1")
                        .or(Entity.getFullColumnName(Property.getProperty(User.class, "role.id")))
                        .eq(ID.valueOf(2))
                        .or(Entity.getFullColumnName(Property.getProperty(User.class, "role.name")))
                        .eq(null);
        assertEquals("Uzer.id = ? or Uzer.name = ? or Uzer$Role.id = ?", condition.toString());
        assertEquals(condition.getParameters().toList(),
                Arrays.asList(ID.valueOf(1), "Person 1", ID.valueOf(2)));
    }

    @Test
    public void testGQN()
    {
        GQN<User> GQN = new GQN<>(User.class, "=id", "%name", "=email");
        CompiledCondition condition =
                GQN.getCondition(new User().setId(ID.valueOf(1)).setName("Person 1"));

        String expected = "0 = 0 and Uzer.id = ? and Uzer.name like ?";
        String result = condition.toString();

        List<Object> parameters = condition.getParameters().toList();
        assertEquals(expected, result);
        assertEquals(2, parameters.size());
        assertEquals(parameters.get(0), ID.valueOf(1));
        assertEquals("%Person 1%", parameters.get(1));
    }

    @Test
    public void testGqnNullTypeThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () ->
                Condition.of("name").eq(User.class, null));
    }

    @Test
    public void testWhen()
    {
        CompiledCondition condition = Condition
                .when(false).expression("id").eq(() -> 1)
                .and("name").eq("Person 1")
                .and("birthdate").gt(null)
                .and().when(false).exists(() -> Select.expression("id").from("Person").build());

        List<Object> parameters = condition.getParameters().toList();
        assertEquals("name = ?", condition.toString());
        assertEquals(1, parameters.size());
        assertEquals("Person 1", parameters.get(0));
    }

    @Test
    public void testPropertyWhenChainSkipsFalseBranches()
    {
        CompiledCondition condition = Condition
                .of(Entity.getFullColumnName(Property.getProperty(User.class, "id")))
                .eq(ID.valueOf(1))
                .and().when(false)
                .expression(Entity.getFullColumnName(Property.getProperty(User.class, "name")))
                .eq(() -> "Person 1")
                .and().when(true).expression(Entity.getFullColumnName(Property.getProperty(User.class, "role.id")))
                .eq(() -> ID.valueOf(2))
                .and(Entity.getFullColumnName(Property.getProperty(User.class, "role.name")))
                .eq(null);

        List<Object> parameters = condition.getParameters().toList();
        assertEquals("Uzer.id = ? and Uzer$Role.id = ?", condition.toString());
        assertEquals(2, parameters.size());
        assertEquals(parameters.get(0), ID.valueOf(1));
        assertEquals(parameters.get(1), ID.valueOf(2));
    }

    @Test
    public void testLazyTypedMethods()
    {
        Condition condition = Condition.of("id").eq(1)
                .and().when(true).expression("eq_col").eq(Integer.class, () -> 2)
                .and().when(true).expression("ne_col").ne(Integer.class, () -> 3)
                .and().when(true).expression("lt_col").lt(Integer.class, () -> 4)
                .and().when(true).expression("le_col").le(Integer.class, () -> 5)
                .and().when(true).expression("gt_col").gt(Integer.class, () -> 6)
                .and().when(true).expression("ge_col").ge(Integer.class, () -> 7)
                .and().when(true).expression("lk_col").lk(Integer.class, () -> 8)
                .and().when(true).expression("rx_col").rx(Integer.class, () -> 9)
                .and().when(true).expression("bw_col").bw(Integer.class, () -> 10);

        assertEquals(
                "id = ? and eq_col = ? and ne_col <> ? and lt_col < ? and le_col <= ? and gt_col > ? and ge_col >= ? and lk_col like ? and rx_col rlike ? and bw_col between ? and ?",
                condition.toString());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, "%8%", "9", 10),
                condition.getParameters().toList());
    }

    @Test
    public void testLazyBwTwoSuppliers()
    {
        Condition condition = Condition.of("id").eq(1)
                .and().when(true).expression("range1").bw(() -> 2, () -> 3)
                .and().when(true).expression("range2").bw(Integer.class, () -> 4, () -> 5);

        assertEquals("id = ? and range1 between ? and ? and range2 between ? and ?", condition.toString());
        assertEquals(List.of(1, 2, 3, 4, 5), condition.getParameters().toList());
    }

    @Test
    public void testLazyMethodsNullValidation()
    {
        assertThrows(NullPointerException.class,
                () -> Condition.of("id").eq(1).and().when(true).expression("x").eq(null, () -> 1));
        assertThrows(NullPointerException.class,
                () -> Condition.of("id").eq(1).and().when(true).expression("x").eq(Integer.class, null));
        assertThrows(NullPointerException.class,
                () -> Condition.of("id").eq(1).and().when(true).expression("x").eq(null));
        assertThrows(NullPointerException.class,
                () -> Condition.of("id").eq(1).and().when(true).expression("x").bw(Integer.class, () -> 1, null));
    }

    @Test
    public void testWhenFalseDoesNotExecuteSupplier()
    {
        AtomicInteger counter = new AtomicInteger();

        Condition condition = Condition.of("id").eq(1)
                .and().when(false).expression("eq_col").eq(counter::incrementAndGet)
                .and().when(false).expression("bw_col").bw(counter::incrementAndGet, counter::incrementAndGet);

        assertEquals(0, counter.get());
        assertEquals("id = ?", condition.toString());
        assertEquals(List.of(1), condition.getParameters().toList());
    }
}
