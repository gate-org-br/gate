package gate.code;

import gate.entity.User;
import gate.io.StringReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ScreenGeneratorTest
{

        @Test
        public void screen() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/screen().doc"));
                String result = new ScreenGenerator(User.class).screen();
                Assert.assertEquals(expected, result);
        }

        @Test
        public void call() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/call().doc"));
                String result = new ScreenGenerator(User.class).call();
                Assert.assertEquals(expected, result);
        }

        @Test
        public void callSelect() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callSelect().doc"));
                String result = new ScreenGenerator(User.class).callSelect();
                Assert.assertEquals(expected, result);
        }

        @Test
        public void callSearch() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callSearch().doc"));
                String result = new ScreenGenerator(User.class).callSearch();
                Assert.assertEquals(expected, result);
        }

        @Test
        public void callInsert() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callInsert().doc"));
                String result = new ScreenGenerator(User.class).callInsert();
                Assert.assertEquals(expected, result);
        }

        @Test
        public void callUpdate() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callUpdate().doc"));
                String result = new ScreenGenerator(User.class).callUpdate();
                Assert.assertEquals(expected, result);
        }

        @Test
        public void callDelete() throws IOException
        {
                String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callDelete().doc"));
                String result = new ScreenGenerator(User.class).callDelete();
                Assert.assertEquals(expected, result);
        }
}
