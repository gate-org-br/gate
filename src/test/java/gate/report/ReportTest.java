package gate.report;

import gate.entity.Role;
import gate.report.doc.XLS;
import gate.type.ID;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class ReportTest
{

	public ReportTest()
	{
	}

	@Test
	@Ignore
	public void testSomeMethod() throws FileNotFoundException, IOException
	{
		List<Role> roles = new ArrayList<>();

		Role role1 = new Role();
		role1.setId(new ID(1));
		role1.setName("Role 1");
		roles.add(role1);

		Role role11 = new Role();
		role11.setId(new ID(11));
		role11.setName("Role 11");
		role1.getRoles().add(role11);

		Role role111 = new Role();
		role111.setId(new ID(11));
		role111.setName("Role 111");
		role11.getRoles().add(role111);

		Role role2 = new Role();
		role2.setId(new ID(2));
		role2.setName("Role 2");
		roles.add(role2);

		Role role21 = new Role();
		role21.setId(new ID(12));
		role21.setName("Role 21");
		role2.getRoles().add(role21);

		Report report = new Report();

		Grid<Role> grid = report.addGrid(Role.class, roles);
		grid.add().head("ID").body(Role::getId);
		grid.add().head("Name").body(Role::getName);

		grid.setChildren(Role::getRoles);

		XLS doc = new XLS(report);
		try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream("Result.xls")))
		{
			doc.print(stream);
		}
	}
}
