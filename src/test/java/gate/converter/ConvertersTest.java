package gate.converter;

import gate.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertersTest
{

    @BeforeAll
    public static void setUp()
    {
        Locale.setDefault(new Locale("pt", "br"));
    }

    @Test
    public void test01()
    {
        Converter converter = Converters.INSTANCE.get(ExtendedExtendedUser.class);
        assertEquals("gate.converter.ObjectConverter", converter.getClass().getName());
    }

    public static class ExtendedUser extends User
    {

    }

    public static class ExtendedExtendedUser extends ExtendedUser
    {

    }

}
