package gate.sql.condition;

import gate.entity.Role;
import gate.sql.select.Select;
import gate.type.LocalDateTimeInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantConditionTest
{

    @Test
    public void testIsNull()
    {
        Condition condition = Condition
                .of("column1").isNull()
                .and().not("column2").isNull()
                .and().when(true).expression("column3").isNull()
                .and().when(true).not("column4").isNull()
                .and().when(false).expression("column5").isNull()
                .and().when(false).not("column6").isNull()
                .and("column7").isNull();

        assertEquals("column1 is null and not column2 is null and column3 is null and not column4 is null and column7 is null",
                condition.toString());
        assertEquals(0, condition.getParameters().count());

    }

    @Test
    public void testIsEq()
    {
        Condition condition = Condition
                .of("column1").isEq("value1")
                .and().not("column2").isEq("value2")
                .and().when(true).expression("column3").isEq("value3")
                .and().when(true).not("column4").isEq("value4")
                .and().when(false).expression("column5").isEq("value5")
                .and().when(false).not("column6").isEq("value6")
                .and("column7").isEq("value7");

        assertEquals("column1 = value1 and not column2 = value2 and column3 = value3 and not column4 = value4 and column7 = value7",
                condition.toString());
        assertEquals(0, condition.getParameters().count());

    }

    @Test
    public void testIsNe()
    {
        Condition condition = Condition
                .of("column1").isNe("value1")
                .and().not("column2").isNe("value2")
                .and().when(true).expression("column3").isNe("value3")
                .and().when(true).not("column4").isNe("value4")
                .and().when(false).expression("column5").isNe("value5")
                .and().when(false).not("column6").isNe("value6")
                .and("column7").isNe("value7");

        assertEquals("column1 <> value1 and not column2 <> value2 and column3 <> value3 and not column4 <> value4 and column7 <> value7",
                condition.toString());
        assertEquals(0, condition.getParameters().count());
    }

    @Test
    public void testIsLt()
    {
        Condition condition = Condition
                .of("column1").isLt("value1")
                .and().not("column2").isLt("value2")
                .and().when(true).expression("column3").isLt("value3")
                .and().when(true).not("column4").isLt("value4")
                .and().when(false).expression("column5").isLt("value5")
                .and().when(false).not("column6").isLt("value6")
                .and("column7").isLt("value7");

        assertEquals("column1 < value1 and not column2 < value2 and column3 < value3 and not column4 < value4 and column7 < value7",
                condition.toString());
        assertEquals(0, condition.getParameters().count());

    }

    @Test
    public void testIsLe()
    {
        Condition condition = Condition
                .of("column1").isLe("value1")
                .and().not("column2").isLe("value2")
                .and().when(true).expression("column3").isLe("value3")
                .and().when(true).not("column4").isLe("value4")
                .and().when(false).expression("column5").isLe("value5")
                .and().when(false).not("column6").isLe("value6")
                .and("column7").isLe("value7");

        assertEquals("column1 <= value1 and not column2 <= value2 and column3 <= value3 and not column4 <= value4 and column7 <= value7",
                condition.toString());
        assertTrue(condition.getParameters().findAny().isEmpty());

    }

    @Test
    public void testIsGt()
    {
        Condition condition = Condition
                .of("column1").isGt("value1")
                .and().not("column2").isGt("value2")
                .and().when(true).expression("column3").isGt("value3")
                .and().when(true).not("column4").isGt("value4")
                .and().when(false).expression("column5").isGt("value5")
                .and().when(false).not("column6").isGt("value6")
                .and("column7").isGt("value7");

        assertEquals("column1 > value1 and not column2 > value2 and column3 > value3 and not column4 > value4 and column7 > value7",
                condition.toString());
        assertEquals(0, condition.getParameters().count());

    }

    @Test
    public void testIsGe()
    {
        Condition condition = Condition
                .of("column1").isGe("value1")
                .and().not("column2").isGe("value2")
                .and().when(true).expression("column3").isGe("value3")
                .and().when(true).not("column4").isGe("value4")
                .and().when(false).expression("column5").isGe("value5")
                .and().when(false).not("column6").isGe("value6")
                .and("column7").isGe("value7");

        assertEquals("column1 >= value1 and not column2 >= value2 and column3 >= value3 and not column4 >= value4 and column7 >= value7",
                condition.toString());
        assertEquals(0, condition.getParameters().count());
    }

    @Test
    public void testIsBw()
    {
        Condition condition = Condition
                .of("column1").isBw("ge1", "le1")
                .and().not("column2").isBw("ge2", "le2")
                .and().when(true).expression("column3").isBw("ge3", "le3")
                .and().when(true).not("column4").isBw("ge4", "le4")
                .and().when(false).expression("column5").isBw("ge5", "le5")
                .and().when(false).not("column6").isBw("ge6", "le6")
                .and("column7").isBw("ge7", "le7");
        assertEquals("column1 between ge1 and le1 and not column2 between ge2 and le2 and column3 between ge3 and le3 and not column4 between ge4 and le4 and column7 between ge7 and le7",
                condition.toString());
        assertEquals(0, condition.getParameters().count());
    }

    @Test
    public void testEqClassTypedValue()
    {
        try
        {
            Condition.of("column1").eq(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").eq(Integer.class, 1);
            assertEquals("column1 = ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
        }
    }

    @Test
    public void testIsEqQuery()
    {
        Condition condition1 = Condition.of("column1").isEq(Select.expression("column2").from("table1").where(Condition.of("column3").eq()));
        assertEquals("column1 = (select column2 from table1 where column3 = ?)", condition1.toString());
        assertEquals(0, condition1.getParameters().count());
    }

    @Test
    public void testIsEqCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isEq(Select.expression("column2").from("table1").where(Condition.of("column3").eq(3)));
        assertEquals("column1 = (select column2 from table1 where column3 = ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testNeClassTypedValue()
    {
        try
        {
            Condition.of("column1").ne(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").ne(Integer.class, 1);
            assertEquals("column1 <> ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
        }
    }

    @Test
    public void testIsNeQuery()
    {
        Condition condition1 = Condition.of("column1").isNe(Select.expression("column2").from("table1").where(Condition.of("column3").ne()));
        assertEquals("column1 <> (select column2 from table1 where column3 <> ?)", condition1.toString());
        assertEquals(0, condition1.getParameters().count());
    }

    @Test
    public void testIsNeCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isNe(Select.expression("column2").from("table1").where(Condition.of("column3").ne(3)));
        assertEquals("column1 <> (select column2 from table1 where column3 <> ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testLtClassTypedValue()
    {
        try
        {
            Condition.of("column1").lt(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").lt(Integer.class, 1);
            assertEquals("column1 < ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
        }
    }

    @Test
    public void testIsLtQuery()
    {
        Condition condition1 = Condition.of("column1").isLt(Select.expression("column2").from("table1").where(Condition.of("column3").lt()));
        assertEquals("column1 < (select column2 from table1 where column3 < ?)", condition1.toString());
        assertEquals(0, condition1.getParameters().count());
    }

    @Test
    public void testIsLtCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isLt(Select.expression("column2").from("table1").where(Condition.of("column3").lt(3)));
        assertEquals("column1 < (select column2 from table1 where column3 < ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testLeObjectValue()
    {
        Condition condition1 = Condition.of("column1").le(1);
        assertEquals("column1 <= ?", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(1));

        Condition condition2 = Condition.of("column1").eq(null).and("column2").le(2);
        assertEquals("column2 <= ?", condition2.toString());
        assertEquals(condition2.getParameters().collect(Collectors.toList()), List.of(2));

    }

    @Test
    public void testLeClassTypedValue()
    {
        try
        {
            Condition.of("column1").le(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").le(Integer.class, 1);
            assertEquals("column1 <= ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
        }
    }

    @Test
    public void testIsLeQuery()
    {
        Condition condition1 = Condition.of("column1").isLe(Select.expression("column2").from("table1").where(Condition.of("column3").le()));
        assertEquals("column1 <= (select column2 from table1 where column3 <= ?)", condition1.toString());
        assertEquals(0, condition1.getParameters().count());
    }

    @Test
    public void testIsLeCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isLe(Select.expression("column2").from("table1").where(Condition.of("column3").le(3)));
        assertEquals("column1 <= (select column2 from table1 where column3 <= ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testGtClassTypedValue()
    {
        try
        {
            Condition.of("column1").gt(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").gt(Integer.class, 1);
            assertEquals("column1 > ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
        }
    }

    @Test
    public void testIsGtQuery()
    {
        Condition condition1 = Condition.of("column1").isGt(Select.expression("column2").from("table1").where(Condition.of("column3").gt()));
        assertEquals("column1 > (select column2 from table1 where column3 > ?)", condition1.toString());
        assertEquals(0, condition1.getParameters().count());
    }

    @Test
    public void testIsGtCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isGt(Select.expression("column2").from("table1").where(Condition.of("column3").gt(3)));
        assertEquals("column1 > (select column2 from table1 where column3 > ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testGeObjectValue()
    {
        Condition condition1 = Condition.of("column1").ge(1);
        assertEquals("column1 >= ?", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(1));

        Condition condition2 = Condition.of("column1").eq(null).and("column2").ge(2);
        assertEquals("column2 >= ?", condition2.toString());
        assertEquals(condition2.getParameters().collect(Collectors.toList()), List.of(2));

    }

    @Test
    public void testGeClassTypedValue()
    {
        try
        {
            Condition.of("column1").ge(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").ge(Integer.class, 1);
            assertEquals("column1 >= ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
        }
    }

    @Test
    public void testIsGeQuery()
    {
        Condition condition1 = Condition.of("column1").isGe(Select.expression("column2").from("table1").where(Condition.of("column3").ge()));
        assertEquals("column1 >= (select column2 from table1 where column3 >= ?)", condition1.toString());
        assertEquals(0, condition1.getParameters().count());
    }

    @Test
    public void testIsGeCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isGe(Select.expression("column2").from("table1").where(Condition.of("column3").ge(3)));
        assertEquals("column1 >= (select column2 from table1 where column3 >= ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testBwWithoutArguments()
    {
        Condition condition = Condition.of("column1").bw();
        assertEquals("column1 between ? and ?", condition.toString());
        assertEquals(0, condition.getParameters().count());
    }

    @Test
    public void testBwObjectValue()
    {
        LocalDateTime min = LocalDateTime.of(1500, 4, 22, 0, 0, 0);
        LocalDateTime max = LocalDateTime.of(1989, 11, 15, 0, 0, 0);
        LocalDateTimeInterval dateTimeInterval = LocalDateTimeInterval.of(min, max);

        Condition condition1 = Condition.of("column1").bw(dateTimeInterval);
        assertEquals("column1 between ? and ?", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(dateTimeInterval));

        Condition condition2 = Condition.of("column1").bw(null).and("column2").bw(dateTimeInterval);
        assertEquals("column2 between ? and ?", condition2.toString());
        assertEquals(condition2.getParameters().collect(Collectors.toList()), List.of(dateTimeInterval));

    }

    @Test
    public void testBwClassTypedValue()
    {
        try
        {
            Condition.of("column1").bw(Integer.class, null);
            fail();
        } catch (NullPointerException ex)
        {

            LocalDateTime min = LocalDateTime.of(1500, 4, 22, 0, 0, 0);
            LocalDateTime max = LocalDateTime.of(1989, 11, 15, 0, 0, 0);
            LocalDateTimeInterval dateTimeInterval = LocalDateTimeInterval.of(min, max);

            Condition condition = Condition.of("column1").bw(LocalDateTimeInterval.class, dateTimeInterval);
            assertEquals("column1 between ? and ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(dateTimeInterval));
        }
    }

    @Test
    public void testBwTwoObjectValues()
    {
        LocalDateTime min = LocalDateTime.of(1500, 4, 22, 0, 0, 0);
        LocalDateTime max = LocalDateTime.of(1989, 11, 15, 0, 0, 0);

        Condition condition1 = Condition.of("column1").bw(min, max);
        assertEquals("column1 between ? and ?", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(min, max));

        Condition condition2 = Condition
                .of("column1").bw(min, null)
                .and("column2").bw((Object) null, max)
                .and("column3").bw(min, max);
        assertEquals("column3 between ? and ?", condition2.toString());
        assertEquals(condition2.getParameters().collect(Collectors.toList()), List.of(min, max));
    }

    @Test
    public void testBwClassThreeArguments()
    {
        try
        {
            Condition.of("column1").bw(LocalDateTime.class, null, null);
            fail();
        } catch (NullPointerException ex)
        {
            LocalDateTime min = LocalDateTime.of(1500, 4, 22, 0, 0, 0);
            LocalDateTime max = LocalDateTime.of(1989, 11, 15, 0, 0, 0);
            Condition condition = Condition.of("column1").bw(LocalDateTime.class, min, max);
            assertEquals("column1 between ? and ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(min, max));
        }
    }

    @Test
    public void testInList()
    {
        List<Integer> values = List.of(1, 2, 3);

        Condition condition1 = Condition.of("column1").in(values);
        assertEquals("column1 in (?, ?, ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), values);

        Condition condition2 = Condition.of("column1").in(null).and("column2").in(values);
        assertEquals("column2 in (?, ?, ?)", condition2.toString());
        assertEquals(condition2.getParameters().collect(Collectors.toList()), values);
    }

    @Test
    public void testInClassList()
    {
        try
        {
            Condition.of("column1").in(String.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            List<String> values = List.of("value1", "value2", "value2");
            Condition condition = Condition.of("column1").in(String.class, values);
            assertEquals("column1 in (?, ?, ?)", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), values);
        }
    }

    @Test
    public void testIsInCompiledQuery()
    {
        Condition condition1 = Condition.of("column1").isIn(Select.expression("column2").from("table1").where(Condition.of("column3").ne(3)));
        assertEquals("column1 in (select column2 from table1 where column3 <> ?)", condition1.toString());
        assertEquals(condition1.getParameters().collect(Collectors.toList()), List.of(3));
    }

    @Test
    public void testLkClassTypedValue()
    {
        try
        {
            Condition.of("column1").lk(String.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").lk(String.class, "value1");
            assertEquals("column1 like ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of("%value1%"));
        }
    }

    @Test
    public void testRxClassTypedValue()
    {
        try
        {
            Condition.of("column1").rx(String.class, null);
            fail();
        } catch (NullPointerException ex)
        {
            Condition condition = Condition.of("column1").rx(String.class, "value1");
            assertEquals("column1 rlike ?", condition.toString());
            assertEquals(condition.getParameters().collect(Collectors.toList()), List.of("value1"));
        }
    }

    @Test
    public void testConstantExists()
    {
        Condition condition = Condition.of("Role.id").eq(1)
                .and().exists(Select
                        .expression("id")
                        .from("Cliente")
                        .where(Condition
                                .of("id").isEq("1")));
        assertEquals("Role.id = ? and exists (select id from Cliente where id = 1)", condition.toString());
        assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
    }

    @Test
    public void testSubqueryEqObject()
    {
        Condition condition = Condition
                .of("Role.id").eq(1)
                .and().when(true)
                .subquery(() -> Select
                        .expression("id")
                        .from("Cliente")
                        .where(Condition.of("id").isEq("1"))).eq(() -> 2);
        assertEquals("Role.id = ? and (select id from Cliente where id = 1) = ?", condition.toString());
        assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1, 2));

        condition = Condition.of("Role.id").eq(1)
                .and().when(false).subquery(() -> Select
                        .expression("id")
                        .from("Cliente")
                        .where(Condition
                                .of("id").isEq("1"))).eq(() -> 2);
        assertEquals("Role.id = ?", condition.toString());
        assertEquals(condition.getParameters().collect(Collectors.toList()), List.of(1));
    }

    @Test
    public void testSubqueryIsEq()
    {
        String expected = "Role.id = ? and (select id from Cliente where id = 1) = 1";
        String result = Condition.of("Role.id").eq(1)
                .and().when(true).subquery(() -> Select
                        .expression("id")
                        .from("Cliente")
                        .where(Condition.of("id").isEq("1"))).isEq("1")
                .toString();
        assertEquals(expected, result);
    }

    @Test
    public void testFrom()
    {
        String expected = "Role.id = ?";
        String result = Condition.from(Role.class).expression("Role.id").eq(Role::getId)
                .toString();
        assertEquals(expected, result);
    }
}
