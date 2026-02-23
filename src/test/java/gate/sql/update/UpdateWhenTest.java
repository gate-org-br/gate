package gate.sql.update;

import gate.Person;
import gate.lang.property.Property;
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

        TableUpdate.Generic generic = Update.table("Person").set("name");
        TableUpdate.Generic.When genericWhen = generic.new When();
        genericWhen.when(true).set("birthdate");
        genericWhen.when(false).set("ignored");
        assertEquals("update Person set name = ?, birthdate = ?", generic.toString());

        TableUpdate.Compiled compiled = Update.table("Person")
                .set("name", "Alice");
        compiled.when(true).setIfTrue("birthdate", () -> LocalDate.of(2000, 1, 1))
                .when(false).setIfTrue("ignored", () -> "x");
        assertEquals("update Person set name = ?, birthdate = ?", compiled.toString());
        assertEquals("update Person set name = ?, birthdate = ?", compiled.build().toString());
    }

    @Test
    public void testPreparedWhenAndWhereLimit()
    {
        TableUpdate.Prepared<Person> prepared = Update.table("Person")
                .from(Person.class)
                .set("name", Person::getName);

        TableUpdate.Prepared<Person>.When preparedWhen = prepared.new When();
        preparedWhen.when(true).set("birthdate", Person::getBirthdate);
        preparedWhen.when(false).set("ignored", Person::getBirthdate);

        assertEquals("update Person set name = ?, birthdate = ?", prepared.toString());

        var whereBuilder = prepared.where(Condition.from(Person.class).expression("id").eq(Person::getId));
        assertEquals("update Person set name = ?, birthdate = ? where id = ?", whereBuilder.toString());
    }

    @Test
    public void testClassAndObjectUpdateWhenBranches()
    {
        ClassUpdate<Person> classUpdate = Update.table(Person.class);

        ClassUpdate<Person>.Compiled disabledTop = classUpdate.when(false)
                .set(Person::getName, "Ignored");
        assertEquals("update Person set ", disabledTop.toString());

        ClassUpdate<Person>.Generic generic = classUpdate.set(Person::getName);
        ClassUpdate<Person>.Generic.When genericWhen = generic.new When();
        genericWhen.when(true).set(Person::getBirthdate);
        genericWhen.when(false).set(Person::getContract);
        assertEquals("update Person set name = ?, birthdate = ?", generic.toString());

        ClassUpdate<Person>.Compiled compiled = classUpdate.set(Person::getName, "Bob");
        compiled.when(true).set(Person::getBirthdate, LocalDate.of(2001, 1, 1))
                .when(false).set(Person::getContract, null);
        assertEquals("update Person set name = ?, birthdate = ?", compiled.toString());

        ObjectUpdate<Person> objectUpdate = classUpdate.from(
                new Person().setName("Carol").setBirthdate(LocalDate.of(2002, 2, 2))
        );

        ObjectUpdate<Person>.Compiled objectCompiled = objectUpdate.compiled();
        objectCompiled.when(true).set(Person::getName).when(false).set(Person::getBirthdate);
        assertEquals("update Person set name = ?", objectCompiled.toString());
    }

    @Test
    public void testTypedUpdateLimitBuilders()
    {
        TypedUpdate<Person> update = Update.type(Person.class);

        String limitedGeneric = update.set("name")
                .limit(1)
                .build()
                .toString();
        assertEquals("update Person set name = ? limit 1", limitedGeneric);

        String limitedConstantWhere = update.set("name")
                .where(Condition.of("id").isEq("1"))
                .limit(2)
                .build()
                .toString();
        assertEquals("update Person set name = ? where id = 1 limit 2", limitedConstantWhere);

        String limitedGenericWhere = update.set("name")
                .where(Condition.of("id").isEq(Property.getProperty(Person.class, "id")))
                .limit(3)
                .build()
                .toString();
        assertEquals("update Person set name = ? where id = ? limit 3", limitedGenericWhere);
    }
}
