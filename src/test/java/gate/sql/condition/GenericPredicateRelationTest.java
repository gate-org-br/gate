package gate.sql.condition;

import gate.sql.statement.Query;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenericPredicateRelationTest
{

    @Test
    public void testGenericPredicateSubqueryComparisons()
    {
        Query subquery = Query.of("select max(score) from scores");

        GenericCondition condition = Condition.of("score").eq()
                .and("threshold").isGe(subquery)
                .or().expression("rank").isLt(subquery);

        assertEquals("score = ? and threshold >= (select max(score) from scores) or rank < (select max(score) from scores)",
                condition.toString());
        assertTrue(condition.getParameters().toList().isEmpty());
    }

    @Test
    public void testGenericRelationWhenFalseRollback()
    {
        GenericCondition condition = Condition.of("a").eq()
                .and().when(false).expression("b").ne()
                .or("c").gt();

        assertEquals("a = ? or c > ?", condition.toString());
        assertEquals(List.of(), condition.getParameters().toList());
    }

    @Test
    public void testGenericPredicateNullBuilderValidation()
    {
        assertThrows(NullPointerException.class, () ->
                Condition.of("x").eq().and("y").isEq((Query.Builder) null));
        assertThrows(NullPointerException.class, () ->
                Condition.of("x").eq().and("y").isNe((Query.Builder) null));
        assertThrows(NullPointerException.class, () ->
                Condition.of("x").eq().and("y").isLt((Query.Builder) null));
        assertThrows(NullPointerException.class, () ->
                Condition.of("x").eq().and("y").isLe((Query.Builder) null));
        assertThrows(NullPointerException.class, () ->
                Condition.of("x").eq().and("y").isGt((Query.Builder) null));
        assertThrows(NullPointerException.class, () ->
                Condition.of("x").eq().and("y").isGe((Query.Builder) null));
    }

    @Test
    public void testGenericRelationQueryAndBuilderOverloads()
    {
        Query subquery = Query.of("select id from role");
        Query.Builder builder = () -> Query.of("select role_id from user_role");

        GenericCondition condition = Condition.of("x").eq()
                .and().subquery(subquery).eq()
                .and().not(subquery).ne()
                .and().exists(subquery)
                .and().subquery(builder).lt()
                .and().not(builder).gt()
                .and().exists(builder);

        assertEquals("x = ? and (select id from role) = ? and  not (select id from role) <> ? and exists (select id from role) and (select role_id from user_role) < ? and  not (select role_id from user_role) > ? and exists (select role_id from user_role)",
                condition.toString());
        assertTrue(condition.getParameters().toList().isEmpty());
    }

    @Test
    public void testGenericRelationQueryRollbackOverloads()
    {
        Query subquery = Query.of("select id from role");
        Query.Builder builder = () -> Query.of("select role_id from user_role");

        GenericCondition condition = Condition.of("start").eq()
                .and().when(false).subquery(subquery).eq()
                .and().when(false).not(subquery).eq()
                .and().when(false).exists(subquery)
                .and().when(false).subquery(builder).eq()
                .and().when(false).not(builder).eq()
                .and().when(false).exists(builder)
                .or("end_col").ne();

        assertEquals("start = ? or end_col <> ?", condition.toString());
        assertTrue(condition.getParameters().toList().isEmpty());
    }
}
