package gate.base;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScreenOrderByTest
{

	private static final List<String> VALID = List.of(
			"name", "Central.id", "role.name", "Operador.name", "Criacao.data", "pausas.size()",
			"getDuracao()", "getDuracaoLiquida()", "isActive()", "this[0]", "this[4]", "Central$id",
			"centroCustoDestino", "user.name", "duracao()", "ativo()");

	private static final List<String> INVALID = List.of(
			// SQL
			"date desc, (select 1)",
			"(select sleep(5))",
			"if(1=1,date,id)",
			"date;drop table Call",
			"date--",
			"date/**/desc",
			"`date`",
			"date desc",
			"1",
			"date,id",
			// reflection
			"getClass()",
			"getClass().getName()",
			"name.getClass()",
			"toString()",
			"wait()",
			"hashCode()",
			"notify()",
			"notifyAll()",
			"clone()",
			"getDuration(1)",
			"get('x')",
			"forName('java.lang.Runtime')",
			"this[0]x",
			"this[a]",
			// format
			"",
			"+",
			"++date",
			".date",
			"date.",
			"date..name",
			" date",
			"date\n");

	@Test
	void acceptsPropertyPaths()
	{
		for (String value : VALID)
			for (String sign : List.of("", "+", "-"))
				assertTrue(Screen.ORDER_BY.matcher(sign + value).matches(), sign + value);
	}

	@Test
	void rejectsSqlAndReflection()
	{
		for (String value : INVALID)
			assertFalse(Screen.ORDER_BY.matcher(value).matches(), value);
	}

	@Test
	void rejectsLongValues()
	{
		assertFalse(Screen.ORDER_BY.matcher("a".repeat(129)).matches());
	}

	@Test
	void getOrderByDiscardsInvalidValues()
	{
		Screen screen = new Screen()
		{
		};

		screen.setOrderBy("-Central.id");
		assertEquals("-Central.id", screen.getOrderBy());

		screen.setOrderBy("(select sleep(5))");
		assertNull(screen.getOrderBy());

		screen.setOrderBy(null);
		assertNull(screen.getOrderBy());
	}
}
