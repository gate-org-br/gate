package gate.sql.condition;

import mock.UserMock;
import gate.sql.select.Select;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompiledConditionTest
{

	@Test
	public void shouldBuildCompiledEqualityPredicate()
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
	public void shouldBuildCompiledInequalityPredicate()
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
	public void shouldBuildCompiledLessThanPredicate()
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
	public void shouldBuildCompiledLessThanOrEqualPredicate()
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
	public void shouldBuildCompiledGreaterThanPredicate()
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
	public void shouldBuildCompiledGreaterThanOrEqualPredicate()
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
	public void shouldBuildCompiledLikePredicate()
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
	public void shouldBuildCompiledRegexPredicate()
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
	public void shouldBuildCompiledBetweenPredicate()
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
	public void shouldSkipNullPredicateInAndChain()
	{
		CompiledCondition condition =
				Condition.of("id").eq(1)
						.and("name").eq("User 1")
						.and("birthdate").gt(null);

		List<Object> parameters = condition.getParameters().toList();
		assertEquals("id = ? and name = ?", condition.toString());
		assertEquals(2, parameters.size());
		assertEquals(1, parameters.get(0));
		assertEquals("User 1", parameters.get(1));
	}

	@Test
	public void shouldSkipNullPredicateInOrChain()
	{
		CompiledCondition condition =
				Condition.of("id").eq(1).or("name").eq("User 1").or("birthdate").gt(null);

		List<Object> parameters = condition.getParameters().toList();
		assertEquals("id = ? or name = ?", condition.toString());
		assertEquals(2, parameters.size());
		assertEquals(1, parameters.get(0));
		assertEquals("User 1", parameters.get(1));
	}


	@Test
	public void shouldRejectNullTypeForQualifiedName()
	{
		assertThrows(NullPointerException.class, () ->
				Condition.of("name").eq(UserMock.class, null));
	}

	@Test
	public void shouldApplyWhenBranchesToCompiledCondition()
	{
		CompiledCondition condition = Condition
				.when(false).expression("id").eq(() -> 1)
				.and("name").eq("User 1")
				.and("birthdate").gt(null)
				.and().when(false).exists(() -> Select.expression("id").from("Uzer").build());

		List<Object> parameters = condition.getParameters().toList();
		assertEquals("name = ?", condition.toString());
		assertEquals(1, parameters.size());
		assertEquals("User 1", parameters.get(0));
	}

	@Test
	public void shouldBuildCompiledConditionFromPropertyReferences()
	{
		Condition condition = Condition.of(UserMock::getName).eq(1)
				.and(UserMock::getActive).eq(2)
				.or(UserMock::getRole).eq(3);

		assertEquals("name = ? and active = ? or Role$id = ?", condition.toString());
		assertEquals(List.of(1, 2, 3), condition.getParameters().toList());
	}

	@Test
	public void shouldEvaluateLazyTypedPredicateSuppliers()
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
	public void shouldEvaluateLazyBetweenSuppliers()
	{
		Condition condition = Condition.of("id").eq(1)
				.and().when(true).expression("range1").bw(() -> 2, () -> 3)
				.and().when(true).expression("range2").bw(Integer.class, () -> 4, () -> 5);

		assertEquals("id = ? and range1 between ? and ? and range2 between ? and ?", condition.toString());
		assertEquals(List.of(1, 2, 3, 4, 5), condition.getParameters().toList());
	}

	@Test
	public void shouldRejectNullLazyPredicateSuppliers()
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
	public void shouldNotExecuteSupplierWhenCompiledConditionIsDisabled()
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
