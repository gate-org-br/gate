package gate.report;

import gate.lang.json.JsonObject;
import gate.report.Report.Orientation;
import gate.doc.PDF;
import gate.doc.XLS;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportTest
{

	static class RoleEntry
	{

		private int id;
		private String name;
		private final List<RoleEntry> roles = new ArrayList<>();

		public int getId() { return id; }
		public String getName() { return name; }
		public List<RoleEntry> getRoles() { return roles; }

		public RoleEntry id(int id) { this.id = id; return this; }
		public RoleEntry name(String name) { this.name = name; return this; }
	}

	static class UserEntry
	{

		private int id;
		private String name;

		public int getId() { return id; }
		public String getName() { return name; }

		public UserEntry id(int id) { this.id = id; return this; }
		public UserEntry name(String name) { this.name = name; return this; }
	}

	@Test
	public void testGrid() throws IOException
	{
		List<RoleEntry> roles = new ArrayList<>();

		RoleEntry role1 = new RoleEntry().id(1).name("Role 1");
		roles.add(role1);

		RoleEntry role11 = new RoleEntry().id(11).name("Role 11");
		role1.getRoles().add(role11);

		RoleEntry role111 = new RoleEntry().id(111).name("Role 111");
		role11.getRoles().add(role111);

		RoleEntry role2 = new RoleEntry().id(2).name("Role 2");
		roles.add(role2);

		RoleEntry role21 = new RoleEntry().id(12).name("Role 21");
		role2.getRoles().add(role21);

		Report report = new Report();

		Grid<RoleEntry> grid = report.addGrid(RoleEntry.class, roles);
		grid.add().head("ID").body(RoleEntry::getId);
		grid.add().head("Name").body(RoleEntry::getName);
		grid.setChildren(RoleEntry::getRoles);

		XLS doc = new XLS(report);
		try (BufferedOutputStream stream = new BufferedOutputStream(new ByteArrayOutputStream()))
		{
			doc.print(stream);
		}
	}

	@Test
	public void testNow() throws IOException
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

		Grid<UserEntry> grid = report.addGrid(UserEntry.class,
						List.of(new UserEntry().id(1).name("Foo"),
								new UserEntry().id(2).name("Bar")))
				.setCaption("USERS: 2");

		grid.add().body(UserEntry::getId).head("ID");
		grid.add().body(UserEntry::getName).head("Name");
		grid.setLimit(2);

		report.compact();
		PDF doc = new PDF(report);
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
		{
			doc.print(stream);
			stream.flush();
		}
	}

	@Test
	public void testList() throws IOException
	{
		Report report = new Report(Orientation.LANDSCAPE);

		report.addHeader(LocalDateTime.now());
		report.addHeader("Report Header");

		report.addLineBreak();

		report.addList()
				.add("Item")
				.add("Item")
				.add("Item")
				.add("Item")
				.style()
				.listStyleType(Style.ListStyleType.DECIMAL);

		report.addList()
				.add("Item")
				.add("Item")
				.add("Item")
				.add("Item")
				.style()
				.listStyleType(Style.ListStyleType.LOWER_ALPHA);

		report.addList()
				.add("Item")
				.add("Item")
				.add("Item")
				.add("Item")
				.style()
				.listStyleType(Style.ListStyleType.DISC);

		report.addList()
				.add("Item")
				.add("Item")
				.add("Item")
				.add("Item")
				.add(report.addList()
						.add("Item")
						.add("Item")
						.add("Item")
						.add("Item")
						.add(report.addList()
								.add("Item")
								.add("Item")
								.add("Item")
								.add("Item")));

		PDF doc = new PDF(report);
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
		{
			doc.print(stream);
			stream.flush();
		}
	}

	@Test
	public void testStyle() throws IOException, URISyntaxException
	{
		var template = JsonObject.parse(Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("style/template.json")).toURI())));
		var report = Report.of(template);
		PDF doc = new PDF(report);

		try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
		{
			doc.print(stream);
			stream.flush();
		}
	}
}
