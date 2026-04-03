package gate.sql.replace;

import gate.Person;
import gate.type.LocalDateInterval;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceTest
{

	@Test
	public void testTableReplaceBuilder()
	{
		TableReplace replace = Replace.into("Person")
				.set("id", 1)
				.set(String.class, "name", "John")
				.set(LocalDateInterval.class, "contract", LocalDateInterval.of(
						LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31)));

		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void testClassReplaceBuilderWithPropertyReference()
	{
		ClassReplace<Person> replace = Replace.into(Person.class)
				.set(Person::getId, 1)
				.set(Person::getName, "Alice")
				.set(Person::getContract, LocalDateInterval.of(
						LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31)));

		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void testClassReplaceBuilderWithObjectAndPropertyReferences()
	{
		Person person = new Person()
				.setName("Carol")
				.setBirthdate(LocalDate.of(2003, 3, 3));

		ClassReplace<Person> replace = Replace.into(Person.class)
				.set(Person::getId, 3)
				.set(person, List.of(Person::getName, Person::getBirthdate));

		assertEquals("replace into Person (id, name, birthdate) values (?, ?, ?)", replace.toString());
		assertEquals("replace into Person (id, name, birthdate) values (?, ?, ?)", replace.build().toString());
	}

	@Test
	public void testObjectReplaceBuilderWithPropertyReferenceAndExtractor()
	{
		Person person = new Person()
				.setId(2)
				.setName("Bob")
				.setBirthdate(LocalDate.of(2002, 2, 2))
				.setContract(LocalDateInterval.of(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31)));

		ObjectReplace<Person> replace = Replace.into(person)
				.set(Person::getId)
				.set(Person::getName, p -> p.getName().toUpperCase())
				.set(Person::getContract);

		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void testObjectReplaceBuilderWithPropertyReferenceList()
	{
		Person person = new Person()
				.setId(4)
				.setName("Dave")
				.setBirthdate(LocalDate.of(2004, 4, 4));

		ObjectReplace<Person> replace = Replace.into(person)
				.set(List.of(Person::getId, Person::getName, Person::getBirthdate));

		assertEquals("replace into Person (id, name, birthdate) values (?, ?, ?)", replace.toString());
		assertEquals("replace into Person (id, name, birthdate) values (?, ?, ?)", replace.build().toString());
	}

	@Test
	public void testBatchReplaceBuilderFromClass()
	{
		BatchReplace<Person> replace = Replace.into(Person.class)
				.from()
				.set(Person::getId)
				.set(Person::getName)
				.set(Person::getContract);

		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.toString());
		assertEquals(
				"replace into Person (id, name, contract__min, contract__max) values (?, ?, ?, ?)",
				replace.build().toString());
	}

	@Test
	public void testBatchReplaceBuilderFromTable()
	{
		BatchReplace<Person> replace = Replace.into("Person")
				.from(Person.class)
				.set("id", Person::getId)
				.set(String.class, "name", Person::getName)
				.set(Person::getBirthdate);

		assertEquals("replace into Person (id, name, birthdate) values (?, ?, ?)", replace.toString());
		assertEquals("replace into Person (id, name, birthdate) values (?, ?, ?)", replace.build().toString());
	}
}
