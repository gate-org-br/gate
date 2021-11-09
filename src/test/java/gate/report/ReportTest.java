package gate.report;

import gate.entity.Role;
import gate.entity.User;
import gate.report.Report.Orientation;
import gate.report.doc.PDF;
import gate.report.doc.XLS;
import gate.type.ID;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ReportTest
{

	public ReportTest()
	{
	}

	@Test
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
		try ( BufferedOutputStream stream = new BufferedOutputStream(new ByteArrayOutputStream()))
		{
			doc.print(stream);
		}

	}

	@Test
	public void testNow() throws FileNotFoundException, IOException
	{
		Report report = new Report(Orientation.LANDSCAPE);

		report.addHeader(LocalDateTime.now());
		report.addHeader("Report Header");

		report.addLineBreak();

		Form form = report.addForm(8);

		form.setCaption("Filtro");
		form.add("Category", "Computer").colspan(2);
		form.add("Manufacturer", "HP").colspan(2);
		form.add("Model", "Pavilon 1232").colspan(2);
		form.add("Platelet", null);
		form.add("Serial Number", null);
		form.add("Supplier", "HP");
		form.add("Desciption", "A nice computer").colspan(8);

		report.addLineBreak();
		report.addLineBreak();
		report.addLineBreak();
		report.addLineBreak();
		report.addLineBreak();

		form = report.addForm(8);
		form.setCaption("Empty");
		form.add("Category", null).colspan(2);
		form.add("Manufacturer", null).colspan(2);
		form.add("Model", null).colspan(2);
		form.add("Platelet", null);
		form.add("Serial Number", null);
		form.add("Supplier", null);
		form.add("Desciption", null).colspan(8);

		report.addLineBreak();

		Grid<User> grid = report.addGrid(User.class,
			List.of(new User().setId(new ID(1)).setName("Foo"),
				new User().setId(new ID(2)).setName("Bar")))
			.setCaption("USERS: 2");

		grid.add().body(User::getName).head("ID");
		grid.add().body(User::getName).head("Name");
		grid.add().body(User::getCellPhone).head("CellPhone");
		grid.setLimit(2);

		report.compact();
		PDF doc = new PDF(report);
		try ( BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream("/home/davins/Desktop/test.pdf")))
		{
			doc.print(stream);
		}
	}
}
