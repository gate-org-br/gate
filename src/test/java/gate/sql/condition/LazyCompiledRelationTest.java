package gate.sql.condition;

import gate.sql.select.Select;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LazyCompiledRelationTest
{

    @Test
    public void testWhenTruePreservesConnectors()
    {
        Condition condition = Condition.of("column1").eq(1)
                .and().not("column2").eq(2)
                .or().when(true).expression("column3").eq(() -> 3)
                .and().when(true).not("column4").eq(() -> 4)
                .or("column5").eq(5);

        assertEquals("column1 = ? and not column2 = ? or column3 = ? and not column4 = ? or column5 = ?",
                condition.toString());
        assertEquals(List.of(1, 2, 3, 4, 5), condition.getParameters().toList());
    }

    @Test
    public void testWhenFalseSkipsLazySuppliers()
    {
        AtomicInteger counter = new AtomicInteger();

        Condition condition = Condition.of("id").eq(1)
                .and().when(false).expression("eq_col").eq(counter::incrementAndGet)
                .and().when(false).not("bw_col").bw(counter::incrementAndGet, counter::incrementAndGet);

        assertEquals(0, counter.get());
        assertEquals("id = ?", condition.toString());
        assertEquals(List.of(1), condition.getParameters().toList());
    }

    @Test
    public void testCompiledConditionAndExistsSuppliers()
    {
        Condition condition = Condition.of("id").eq(1)
                .and().when(true).condition((LazyCompiledRelation.CompiledConditionSupplier) () ->
                        Condition.of("name").eq("Person 1"))
                .and().when(true).exists((LazyCompiledRelation.CompiledQuerySupplier) () ->
                        Select.expression("sub_id").from("sub_table")
                                .where(Condition.of("active").eq(2))
                                .build());

        assertEquals("id = ? and (name = ?) and exists (select sub_id from sub_table where active = ?)",
                condition.toString());
        assertEquals(List.of(1, "Person 1", 2), condition.getParameters().toList());
    }
}
