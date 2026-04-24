package gate.sql.replace;

import mock.UserMock;
import gate.type.LocalDateInterval;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceTest
{

	@Test
	public void shouldBuildTableReplaceWithExplicitColumns()
	{
		TableReplace replace = Replace.into("Uzer")
				.set("id", 1)
				.set(String.class, "name", "John")
				.set(LocalDateInterval.class, "contract", LocalDateInterval.of(
						LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31)));

		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void shouldBuildTypedReplaceWithPropertyReferences()
	{
		ClassReplace<UserMock> replace = Replace.into(UserMock.class)
				.set(UserMock::getId, 1)
				.set(UserMock::getName, "Alice")
				.set(UserMock::getContract, LocalDateInterval.of(
						LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31)));

		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void shouldBuildTypedReplaceUsingObjectPropertyValues()
	{
		UserMock person = new UserMock()
				.setName("Carol")
				.setBirthdate(LocalDate.of(2003, 3, 3));

		ClassReplace<UserMock> replace = Replace.into(UserMock.class)
				.set(UserMock::getId, 3)
				.set(person, List.of(UserMock::getName, UserMock::getBirthdate));

		assertEquals("replace into Uzer (id, name, birthdate) values (?, ?, ?)", replace.toString());
		assertEquals("replace into Uzer (id, name, birthdate) values (?, ?, ?)", replace.build().toString());
	}

	@Test
	public void shouldBuildObjectReplaceWithPropertyReferenceExtractor()
	{
		UserMock person = new UserMock()
				.setId(2)
				.setName("Bob")
				.setBirthdate(LocalDate.of(2002, 2, 2))
				.setContract(LocalDateInterval.of(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31)));

		ObjectReplace<UserMock> replace = Replace.into(person)
				.set(UserMock::getId)
				.set(UserMock::getName, p -> p.getName().toUpperCase())
				.set(UserMock::getContract);

		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void shouldBuildObjectReplaceWithPropertyReferenceList()
	{
		UserMock person = new UserMock()
				.setId(4)
				.setName("Dave")
				.setBirthdate(LocalDate.of(2004, 4, 4));

		ObjectReplace<UserMock> replace = Replace.into(person)
				.set(List.of(UserMock::getId, UserMock::getName, UserMock::getBirthdate));

		assertEquals("replace into Uzer (id, name, birthdate) values (?, ?, ?)", replace.toString());
		assertEquals("replace into Uzer (id, name, birthdate) values (?, ?, ?)", replace.build().toString());
	}

	@Test
	public void shouldBuildBatchReplaceFromClass()
	{
		BatchReplace<UserMock> replace = Replace.into(UserMock.class)
				.from()
				.set(UserMock::getId)
				.set(UserMock::getName)
				.set(UserMock::getContract);

		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Uzer (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void shouldBuildBatchReplaceFromTable()
	{
		BatchReplace<UserMock> replace = Replace.into("Uzer")
				.from(UserMock.class)
				.set("id", UserMock::getId)
				.set(String.class, "name", UserMock::getName)
				.set(UserMock::getBirthdate);

		assertEquals("replace into Uzer (id, name, birthdate) values (?, ?, ?)", replace.toString());
		assertEquals("replace into Uzer (id, name, birthdate) values (?, ?, ?)", replace.build().toString());
	}

	@Test
	public void shouldApplyWhenBranchesToTableReplace()
	{
		TableReplace disabled = Replace.into("Uzer")
				.when(false)
				.set("id", () -> 1);

		assertEquals("replace into Uzer () values ()", disabled.toString());
		assertEquals("replace into Uzer () values ()", disabled.build().toString());

		TableReplace compiled = Replace.into("Uzer")
				.when(true)
				.set("id", () -> 1)
				.when(true)
				.set(String.class, "name", () -> "John")
				.when(false)
				.set("birthdate", () -> LocalDate.of(2000, 1, 1));

		assertEquals("replace into Uzer (id, name) values (?, ?)", compiled.toString());
		assertEquals("replace into Uzer (id, name) values (?, ?)", compiled.build().toString());
	}

	@Test
	public void shouldApplyWhenBranchesToTypedReplace()
	{
		ClassReplace<UserMock> replace = Replace.into(UserMock.class);

		ClassReplace<UserMock> disabled = replace.when(false).set(UserMock::getId, () -> 1);
		assertEquals("replace into Uzer () values ()", disabled.toString());
		assertEquals("replace into Uzer () values ()", disabled.build().toString());

		ClassReplace<UserMock> compiled = replace.when(true)
				.set(UserMock::getId, () -> 1)
				.when(true)
				.set(UserMock::getName, () -> "Alice")
				.when(false)
				.set(UserMock::getBirthdate, () -> LocalDate.of(2000, 1, 1));

		assertEquals("replace into Uzer (id, name) values (?, ?)", compiled.toString());
		assertEquals("replace into Uzer (id, name) values (?, ?)", compiled.build().toString());
	}

	@Test
	public void shouldApplyWhenBranchesToObjectReplace()
	{
		UserMock person = new UserMock()
				.setId(10)
				.setName("Bob")
				.setBirthdate(LocalDate.of(2001, 1, 1));

		ObjectReplace<UserMock> replace = Replace.into(person);

		ObjectReplace<UserMock> disabled = replace.when(false).set(UserMock::getId);
		assertEquals("replace into Uzer () values ()", disabled.toString());
		assertEquals("replace into Uzer () values ()", disabled.build().toString());

		ObjectReplace<UserMock> compiled = replace.when(true)
				.set(List.of(UserMock::getId, UserMock::getName))
				.when(false)
				.set(List.of(UserMock::getBirthdate));

		assertEquals("replace into Uzer (id, name) values (?, ?)", compiled.toString());
		assertEquals("replace into Uzer (id, name) values (?, ?)", compiled.build().toString());
	}
}