package gate.constraints;

import gate.Mock;
import gate.constraint.Constraints;
import gate.error.AppException;
import gate.lang.property.Property;
import gate.type.ID;
import java.util.Locale;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConstraintImplTest
{

	@BeforeClass
	public static void startup()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void test1()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("Pessoa 1");

			Constraints.validate(mock, Property.getProperties(Mock.class, "id"));
		} catch (AppException | RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test2()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("Pessoa 1");

			Constraints.validate(mock, Property.getProperties(Mock.class, "id", "name"));
			Assert.fail();
		} catch (AppException ex)
		{
		} catch (RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test3()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("P1");

			Constraints.validate(mock, Property.getProperties(Mock.class, "id", "name"));
		} catch (AppException ex)
		{
			Assert.fail(ex.getMessage());
		} catch (RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test4()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("PA");

			Constraints.validate(mock, Property.getProperties(Mock.class, "id", "name"));
			Assert.fail();
		} catch (AppException ex)
		{
			Assert.assertEquals("O campo name deve estar no formato ^P[0-9]$.",
					ex.getMessage());
		} catch (RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test5()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("P2");
			mock.setAge(20);

			Constraints.validate(mock, Property.getProperties(Mock.class, "id", "name", "age"));
			Assert.fail();
		} catch (AppException ex)
		{
			Assert.assertEquals("O campo age deve ser menor do que 10,0.", ex.getMessage());
		} catch (RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test6()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("P2");
			mock.setAge(9);

			Constraints.validate(mock, Property.getProperties(Mock.class, "id", "name", "age"));
			Assert.fail();
		} catch (AppException ex)
		{
			Assert.assertEquals("O campo age deve ser divisível por 2.", ex.getMessage());
		} catch (RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test7()
	{
		try
		{
			Mock mock = new Mock();
			mock.setId(new ID(1));
			mock.setName("P2");
			mock.setAge(4);

			Constraints.validate(mock, Property.getProperties(Mock.class, "id", "name", "age"));
		} catch (AppException ex)
		{
			Assert.fail(ex.getMessage());
		} catch (RuntimeException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

}
