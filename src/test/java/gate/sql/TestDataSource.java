/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.sql;

import gate.error.ConstraintViolationException;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;

public class TestDataSource
{

	public static volatile LinkSource INSTANCE;

	static
	{
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:gate;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		INSTANCE = LinkSource.of(dataSource);

	}

	public static void setUp() throws ConstraintViolationException, SQLException, ConstraintViolationException
	{

		try (Link link = INSTANCE.getLink())
		{
			link.prepare(TestDataSource.class.getResource("TestDataSource.sql"))
				.execute();
		}

	}
}
