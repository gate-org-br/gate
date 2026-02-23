package gate.sql.insert;

import gate.Person;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertWhenTest
{

    @Test
    public void testTableInsertWhenBranches()
    {
        TableInsert.Compiled disabled = Insert.into("Person")
                .when(false)
                .set("id", () -> 1);

        assertEquals("insert into Person () values ()", disabled.toString());

        TableInsert.Compiled compiled = Insert.into("Person")
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

        ClassInsert<Person>.Generic disabledGeneric = insert.when(false).set(Person::getId);
        assertEquals("insert into Person () values ()", disabledGeneric.toString());

        ClassInsert<Person>.Compiled disabledCompiled = insert.when(false).set(Person::getId, 1);
        assertEquals("insert into Person () values ()", disabledCompiled.toString());
        assertEquals("insert into Person () values ()", disabledCompiled.build().toString());

        ClassInsert<Person>.Generic generic = insert.set(Person::getId);
        ClassInsert<Person>.Generic.When genericWhen = generic.new When();
        genericWhen.set(Person::getName);
        genericWhen.when(false).set(Person::getBirthdate);

        assertEquals("insert into Person (id, name) values (?, ?)", generic.toString());

        ClassInsert<Person>.Compiled compiled = insert.set(Person::getId, 1);
        ClassInsert<Person>.Compiled.When compiledWhen = compiled.new When();
        compiledWhen.set(Person::getName, "Alice");
        compiledWhen.when(false).set(Person::getBirthdate, LocalDate.of(2000, 1, 1));

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

        ObjectInsert<Person> insert = Insert.into(Person.class).from(person);

        ObjectInsert<Person>.Compiled disabled = insert.when(false).set(Person::getId);
        assertEquals("insert into Person () values ()", disabled.toString());
        assertEquals("insert into Person () values ()", disabled.build().toString());

        ObjectInsert<Person>.Compiled compiled = insert.compiled();
        compiled.when(true).set(Person::getId).when(false).set(Person::getName);

        assertEquals("insert into Person (id) values (?)", compiled.toString());
        assertEquals("insert into Person (id) values (?)", compiled.build().toString());
    }
}
