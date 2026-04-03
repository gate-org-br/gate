package gate.sql.replace;

import gate.Person;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceWhenTest
{

	@Test
	public void testTableReplaceWhenBranches()
	{
		TableReplace disabled = Replace.into("Person")
				.when(false)
				.set("id", () -> 1);

		assertEquals("replace into Person () values ()", disabled.toString());
		assertEquals("replace into Person () values ()", disabled.build().toString());

		TableReplace compiled = Replace.into("Person")
				.when(true)
				.set("id", () -> 1)
				.when(true)
				.set(String.class, "name", () -> "John")
				.when(false)
				.set("birthdate", () -> LocalDate.of(2000, 1, 1));

		assertEquals("replace into Person (id, name) values (?, ?)", compiled.toString());
		assertEquals("replace into Person (id, name) values (?, ?)", compiled.build().toString());
	}

	@Test
	public void testClassReplaceWhenBranches()
	{
		ClassReplace<Person> replace = Replace.into(Person.class);

		ClassReplace<Person> disabled = replace.when(false).set(Person::getId, () -> 1);
		assertEquals("replace into Person () values ()", disabled.toString());
		assertEquals("replace into Person () values ()", disabled.build().toString());

		ClassReplace<Person> compiled = replace.when(true)
				.set(Person::getId, () -> 1)
				.when(true)
				.set(Person::getName, () -> "Alice")
				.when(false)
				.set(Person::getBirthdate, () -> LocalDate.of(2000, 1, 1));

		assertEquals("replace into Person (id, name) values (?, ?)", compiled.toString());
		assertEquals("replace into Person (id, name) values (?, ?)", compiled.build().toString());
	}

	@Test
	public void testObjectReplaceWhenBranches()
	{
		Person person = new Person()
				.setId(10)
				.setName("Bob")
				.setBirthdate(LocalDate.of(2001, 1, 1));

		ObjectReplace<Person> replace = Replace.into(person);

		ObjectReplace<Person> disabled = replace.when(false).set(Person::getId);
		assertEquals("replace into Person () values ()", disabled.toString());
		assertEquals("replace into Person () values ()", disabled.build().toString());

		ObjectReplace<Person> compiled = replace.when(true)
				.set(List.of(Person::getId, Person::getName))
				.when(false)
				.set(List.of(Person::getBirthdate));

		assertEquals("replace into Person (id, name) values (?, ?)", compiled.toString());
		assertEquals("replace into Person (id, name) values (?, ?)", compiled.build().toString());
	}
}
