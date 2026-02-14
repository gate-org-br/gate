package gate.sql.delete;

import gate.Contact;
import gate.Mock;
import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.ID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteTest
{

    @BeforeAll
    public static void setUp()
            throws ConstraintViolationException, SQLException
    {
        TestDataSource.setUp();
    }

    @Test
    public void test1()
    {
        String expected = "delete from Uzer where id = ? and name like ?";

        String result = Delete.from("Uzer")
                .where(Condition.of("id").eq()
                        .and("name").lk())
                .build()
                .toString();

        assertEquals(expected, result);
    }

    @Test
    public void test2()
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
    public void test3()
    {
        String expected = "delete from Mock where id = ?";
        String result = Delete.from(Mock.class).build().toString();

        assertEquals(expected, result);
    }

    @Test
    public void test4() throws ConstraintViolationException, SQLException
    {
        try (Link link = TestDataSource.INSTANCE.getLink())
        {
            link
                    .prepare(Delete.from(Person.class))
                    .value(new Person().setId(1))
                    .execute();

            int count = Select.expression("count(*)")
                    .from("Person")
                    .where(Condition.of("id").eq(ID.valueOf(1)))
                    .build()
                    .connect(link)
                    .fetchInt();

            assertEquals(0, count);
        }
    }

    @Test
    public void test5()
    {
        String expected = "delete from Contact where 0 = 0 and Person$id = ?";
        String result = Delete.of(Contact.class, "=person.id").toString();

        assertEquals(expected, result);
    }
}
