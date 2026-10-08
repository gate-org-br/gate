package gate.sql.delete;

import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import mock.Mock;
import mock.UserMock;
import gate.sql.select.Select;
import gate.type.ID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DeleteTest
{

	@BeforeAll
	public static void setUp()
			throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void shouldBuildDeleteFromRawCondition()
	{
		String expected = "delete from Uzer where id = ? and name like ?";

		String result = Delete.from("Uzer")
				.where(Condition.from("id = ? and name like ?"))
				.build()
				.toString();

		assertEquals(expected, result);
	}

	@Test
	public void shouldBuildDeleteFromCompiledCondition()
	{
		String expected = "delete from Uzer where id = ? and name like ?";

		String result = Delete.from("Uzer")
				.where(Condition.of("id").eq(1)
						.and("name").lk("Paulo"))
				.build()
				.toString();

		assertEquals(expected, result);
	}

	@Test
	public void shouldBuildTypedDeleteFromEntityId()
	{
		String expected = "delete from Mock where id = ?";
		String result = Delete.from(Mock.class).build().toString();

		assertEquals(expected, result);
	}

	@Test
	public void shouldExecuteTypedDeleteForEntityList() throws ConstraintViolationException, SQLException
	{

		try (Link link = TestDataSource.getLink())
		{
			try
			{

				link
						.prepare(Delete.from(UserMock.class))
						.execute(List.of(new UserMock().setId(1)));

				assertEquals(0, (int) Select.expression("count(*)").from("Uzer")
						.where(Condition.of("id").isEq(ID.valueOf(1).toString())).build()
						.connect(link).fetchObject(Integer.class).orElseThrow());
			} catch (NullPointerException e)
			{
				fail();
			}
		}
	}

	@Test
	public void shouldBuildDeleteWithForeignKeyCondition()
	{
		String expected = "delete from Contact where User$id = ?";
		String result = Delete.from("Contact").where(Condition.from("User$id = ?")).build().toString();

		assertEquals(expected, result);
	}
}