package gate.sql.update;

import gate.Person;
import gate.sql.condition.Condition;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateWhenTest
{

    @Test
    public void testTableUpdateWhenBranches()
    {
        TableUpdate.Compiled disabled = Update.table("Person")
                .when(false)
                .set("name", "Ignored");

        assertEquals("update Person set ", disabled.toString());

        TableUpdate.Compiled compiled = Update.table("Person")
                .set("name", "Alice");
        compiled.when(true).set("birthdate", () -> LocalDate.of(2000, 1, 1))
                .when(false).set("ignored", () -> "x");
        assertEquals("update Person set name = ?, birthdate = ?", compiled.toString());
        assertEquals("update Person set name = ?, birthdate = ?", compiled.build().toString());
    }

    @Test
    public void testPreparedWhenAndWhereLimit()
    {
        BatchUpdate<Person> prepared = Update.table("Person")
                .from(Person.class)
                .set("name", Person::getName);

        prepared.set("birthdate", Person::getBirthdate);

        assertEquals("update Person set name = ?, birthdate = ?", prepared.toString());

        var whereBuilder = prepared.where(Condition.from(Person.class).expression("id").eq(Person::getId));
        assertEquals("update Person set name = ?, birthdate = ? where id = ?", whereBuilder.toString());
    }

    @Test
    public void testTypedBatchWhenAndWhere()
    {
        BatchUpdate<Person> prepared = Update.table(Person.class)
                .from()
                .set(Person::getName);

        prepared.set(Person::getBirthdate, Person::getBirthdate);

        assertEquals("update Person set name = ?, birthdate = ?", prepared.toString());

        var whereBuilder = prepared.where(Condition.from(Person.class).expression("id").eq(Person::getId));
        assertEquals("update Person set name = ?, birthdate = ? where id = ?", whereBuilder.toString());
    }

    @Test
    public void testClassAndObjectUpdateWhenBranches()
    {
        ClassUpdate<Person> classUpdate = Update.table(Person.class);

        ClassUpdate<Person>.Compiled disabledTop = classUpdate.when(false)
                .set(Person::getName, () -> "Ignored");
        assertEquals("update Person set ", disabledTop.toString());

        ClassUpdate<Person>.Compiled compiled = classUpdate.set(Person::getName, "Bob");
        compiled.when(true).set(Person::getBirthdate, () -> LocalDate.of(2001, 1, 1))
                .when(false).set(Person::getContract, () -> null);
        assertEquals("update Person set name = ?, birthdate = ?", compiled.toString());

        ObjectUpdate<Person> objectUpdate = Update.table(
                new Person().setName("Carol").setBirthdate(LocalDate.of(2002, 2, 2))
        );

        ObjectUpdate<Person>.Compiled objectCompiled = objectUpdate.compiled();
        objectCompiled.when(true).set(Person::getName).when(false).set(Person::getBirthdate);
        assertEquals("update Person set name = ?", objectCompiled.toString());
    }

}
