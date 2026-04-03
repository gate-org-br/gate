package gate.sql.insert;

import gate.Person;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertWhenTest
{

    @Test
    public void testTableInsertWhenBranches()
    {
        TableInsert disabled = Insert.into("Person")
                .when(false)
                .set("id", () -> 1);

        assertEquals("insert into Person () values ()", disabled.toString());

        TableInsert compiled = Insert.into("Person")
                .when(true)
                .set("id", () -> 1)
                .when(true)
                .set(String.class, "name", () -> "John")
                .when(false)
                .set("birthdate", () -> LocalDate.of(2000, 1, 1));

        assertEquals("insert into Person (id, name) values (?, ?)", compiled.toString());
        assertEquals("insert into Person (id, name) values (?, ?)", compiled.build().toString());
    }

    @Test
    public void testClassInsertWhenBranches()
    {
        ClassInsert<Person> insert = Insert.into(Person.class);

        ClassInsert<Person> disabled = insert.when(false).set(Person::getId, () -> 1);
        assertEquals("insert into Person () values ()", disabled.toString());
        assertEquals("insert into Person () values ()", disabled.build().toString());

        ClassInsert<Person> compiled = insert.when(true)
                .set(Person::getId, () -> 1)
                .when(true)
                .set(Person::getName, () -> "Alice")
                .when(false)
                .set(Person::getBirthdate, () -> LocalDate.of(2000, 1, 1));

        assertEquals("insert into Person (id, name) values (?, ?)", compiled.toString());
        assertEquals("insert into Person (id, name) values (?, ?)", compiled.build().toString());
    }

    @Test
    public void testObjectInsertWhenBranches()
    {
        Person person = new Person()
                .setId(10)
                .setName("Bob")
                .setBirthdate(LocalDate.of(2001, 1, 1));

        ObjectInsert<Person> insert = Insert.into(person);

        ObjectInsert<Person> disabled = insert.when(false).set(Person::getId);
        assertEquals("insert into Person () values ()", disabled.toString());
        assertEquals("insert into Person () values ()", disabled.build().toString());

        ObjectInsert<Person> compiled = insert.when(true)
                .set(List.of(Person::getId, Person::getName))
                .when(false)
                .set(List.of(Person::getBirthdate));

        assertEquals("insert into Person (id, name) values (?, ?)", compiled.toString());
        assertEquals("insert into Person (id, name) values (?, ?)", compiled.build().toString());
    }
}
