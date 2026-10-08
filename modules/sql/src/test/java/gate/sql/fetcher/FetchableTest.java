package gate.sql.fetcher;

import gate.error.ConstraintViolationException;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.property.Property;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.statement.Query;
import gate.type.DataGrid;
import gate.type.ID;
import gate.type.LocalDateInterval;
import gate.type.PivotTable;
import gate.util.Page;
import mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FetchableTest
{
	private static final String USERS
			= "select id, name, birthdate, contract__min, contract__max from Uzer order by id";

	@BeforeEach
	public void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void shouldFetchObjectsAndObjectCollections()
	{
		try (Link link = TestDataSource.getLink())
		{
			assertEquals("User 1", Query.of("select name from Uzer order by id").constant().connect(link).fetchObject().orElseThrow());
			assertEquals(ID.valueOf(1), Query.of("select id from Uzer order by id").constant().connect(link).fetchObject(ID.class).orElseThrow());

			assertEquals(List.of("User 1", "User 2", "User 3"),
					Query.of("select name from Uzer where id <= 3 order by id").constant().connect(link).fetchObjectList(String.class));
			assertEquals(List.of("User 1", "User 2", "User 3"),
					Query.of("select name from Uzer where id <= 3 order by id").constant().connect(link).fetchObjectList());

			assertEquals(Set.of(ID.valueOf(1), ID.valueOf(2), ID.valueOf(3)),
					Query.of("select id from Uzer where id <= 3 order by id").constant().connect(link).fetchObjectSet(ID.class));
			assertEquals(Set.of(1L, 2L, 3L),
					Query.of("select id from Uzer where id <= 3 order by id").constant().connect(link).fetchObjectSet());
		}
	}

	@Test
	public void shouldStreamObjectsAndArrays()
	{
		try (Link link = TestDataSource.getLink())
		{
			try (var stream = Query.of("select name from Uzer where id <= 3 order by id").constant().connect(link).objectStream(String.class))
			{
				assertEquals(List.of("User 1", "User 2", "User 3"), stream.toList());
			}

			try (var stream = Query.of("select id, name from Uzer where id = 1").constant().connect(link).arrayStream(Integer.class, String.class))
			{
				Object[] row = stream.findFirst().orElseThrow();
				assertArrayEquals(new Object[]{1, "User 1"}, row);
			}
		}
	}

	@Test
	public void shouldFetchArraysAndArrayCollections()
	{
		try (Link link = TestDataSource.getLink())
		{
			assertArrayEquals(new Object[]{1L, "User 1"},
					Query.of("select id, name from Uzer where id = 1").constant().connect(link).fetchArray().orElseThrow());

			assertArrayEquals(new int[]{1, 2, 3},
					Query.of("select 1 as a, 2 as b, 3 as c").constant().connect(link).fetchIntArray().orElseThrow());

			assertArrayEquals(new Object[]{ID.valueOf(1), "User 1", LocalDate.of(2000, 12, 1)},
					Query.of("select id, name, birthdate from Uzer where id = 1").constant().connect(link)
							.fetchArray(ID.class, String.class, LocalDate.class)
							.orElseThrow());

			List<Object[]> arrays = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchArrayList();
			assertEquals(2, arrays.size());
			assertArrayEquals(new Object[]{1L, "User 1"}, arrays.get(0));

			List<Object[]> typedArrays = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchArrayList(ID.class, String.class);
			assertEquals(2, typedArrays.size());
			assertArrayEquals(new Object[]{ID.valueOf(1), "User 1"}, typedArrays.get(0));

			assertEquals(2, Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link).fetchArraySet().size());
			assertEquals(2, Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchArraySet(ID.class, String.class).size());
		}
	}

	@Test
	public void shouldFetchMapsAndMapCollections()
	{
		try (Link link = TestDataSource.getLink())
		{
			Map<String, Object> map = Query.of("select id, name, birthdate from Uzer where id = 1").constant().connect(link)
					.fetchMap().orElseThrow();
			assertEquals(1L, map.get("id"));
			assertEquals("User 1", map.get("name"));

			Map<String, Object> typedMap = Query.of("select id, name, birthdate from Uzer where id = 1").constant().connect(link)
					.fetchMap(ID.class, String.class, LocalDate.class).orElseThrow();
			assertEquals(ID.valueOf(1), typedMap.get("id"));
			assertEquals(LocalDate.of(2000, 12, 1), typedMap.get("birthdate"));

			List<Map<String, Object>> maps = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchMapList();
			assertEquals(2, maps.size());
			assertEquals("User 1", maps.get(0).get("name"));

			List<Map<String, Object>> typedMaps = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchMapList(ID.class, String.class);
			assertEquals(ID.valueOf(1), typedMaps.get(0).get("id"));

			try (var stream = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link).mapStream(ID.class, String.class))
			{
				assertEquals(List.of(ID.valueOf(1), ID.valueOf(2)),
						stream.map(e -> e.get("id")).toList());
			}
		}
	}

	@Test
	public void shouldFetchMapPages()
	{
		try (Link link = TestDataSource.getLink())
		{
			String sql = "select id, name, count(*) over() as dataSize from Uzer order by id limit 3";

			Page<Map<String, Object>> page = Query.of(sql).constant().connect(link).fetchMapPage(3, 0);
			assertEquals(3, page.size());
			assertEquals(30, page.getPaginator().getDataSize());
			assertEquals("User 1", page.getFrstLine().get("name"));
			assertFalse(page.getFrstLine().containsKey("dataSize"));

			Page<Map<String, Object>> typedPage = Query.of(sql).constant().connect(link).fetchMapPage(3, 0, ID.class, String.class);
			assertEquals(ID.valueOf(1), typedPage.getFrstLine().get("id"));
		}
	}

	@Test
	public void shouldFetchEntitiesAndEntityCollections()
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock user = Query.of(USERS + " limit 1").constant().connect(link).fetchEntity(UserMock.class).orElseThrow();
			assertEquals(1, user.getId());
			assertEquals("User 1", user.getName());
			assertEquals(LocalDate.of(2000, 12, 1), user.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)), user.getContract());

			UserMock contextual = Query.of(USERS + " limit 1").constant().connect(link)
					.fetchEntity(UserMock.class, property -> property.equals("name") ? "Context User" : null)
					.orElseThrow();
			assertEquals("Context User", contextual.getName());

			List<UserMock> users = Query.of(USERS + " limit 3").constant().connect(link).fetchEntityList(UserMock.class);
			assertEquals(3, users.size());
			assertEquals("User 2", users.get(1).getName());

			Set<UserMock> set = Query.of(USERS + " limit 3").constant().connect(link).fetchEntitySet(UserMock.class);
			assertEquals(3, set.size());

			try (var stream = Query.of(USERS + " limit 2").constant().connect(link).entityStream(UserMock.class))
			{
				assertEquals(List.of(1, 2), stream.map(UserMock::getId).toList());
			}
		}
	}

	@Test
	public void shouldFetchEntityPages()
	{
		try (Link link = TestDataSource.getLink())
		{
			String sql = "select id, name, birthdate, contract__min, contract__max, count(*) over() as dataSize "
			             + "from Uzer order by id limit 3";

			Page<UserMock> page = Query.of(sql).constant().connect(link).fetchEntityPage(UserMock.class, 3, 0);
			assertEquals(3, page.size());
			assertEquals(30, page.getPaginator().getDataSize());
			assertEquals("User 1", page.getFrstLine().getName());

			Page<UserMock> contextual = Query.of(sql).constant().connect(link)
					.fetchEntityPage(UserMock.class, 3, 0, property -> property.equals("name") ? "Context User" : null);
			assertEquals("Context User", contextual.getFrstLine().getName());
		}
	}

	@Test
	public void shouldFetchPartialEntities()
	{
		try (Link link = TestDataSource.getLink())
		{
			List<UserMock> users = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchEntityList(UserMock.class, "id", "name");
			assertEquals(2, users.size());
			assertEquals("User 1", users.get(0).getName());
			assertNull(users.get(0).getBirthdate());

			Set<UserMock> set = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchEntitySet(UserMock.class, List.of(
							Property.getProperty(UserMock.class, "id"),
							Property.getProperty(UserMock.class, "name")));
			assertEquals(2, set.size());
		}
	}

	@Test
	public void shouldFetchDataGridsAndPivotTables()
	{
		try (Link link = TestDataSource.getLink())
		{
			DataGrid grid = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link).fetchDataGrid();
			assertArrayEquals(new String[]{"id", "name"}, grid.getHead());
			assertArrayEquals(new Object[]{1L, "User 1"}, grid.get(0));

			DataGrid typedGrid = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link)
					.fetchDataGrid(ID.class, String.class);
			assertEquals(ID.valueOf(1), typedGrid.get(0)[0]);

			PivotTable<Integer> pivot = Query.of(
							"select 'users' as rowLabel, type as colLabel, count(*) as total from Contact group by type")
					.constant().connect(link)
					.fetchPivotTable(Integer.class, 0);
			assertEquals(30, pivot.get("users", "PHONE"));
			assertEquals(0, pivot.get("users", "EMAIL"));
		}
	}

	@Test
	public void shouldFetchJsonValues()
	{
		try (Link link = TestDataSource.getLink())
		{
			JsonObject object = Query.of("select id, name from Uzer where id = 1").constant().connect(link).fetchJsonObject().orElseThrow();
			assertEquals(1, object.getInt("id").orElseThrow());
			assertEquals("User 1", object.getString("name").orElseThrow());

			JsonArray array = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link).fetchJsonArray();
			assertEquals(2, array.size());
			assertEquals("User 2", ((JsonObject) array.get(1)).getString("name").orElseThrow());

			JsonArray dataset = Query.of("select id, name from Uzer where id <= 2 order by id").constant().connect(link).fetchJsonDataset(true);
			assertEquals(List.of("id", "name"), dataset.get(0).unwrap());
			assertEquals(List.of(BigDecimal.ONE, "User 1"), dataset.get(1).unwrap());

			JsonArray tree = Query.of("select cast(null as varchar) as parent, '1' as \"value\", 'Root' as label "
			                          + "union all select '1' as parent, '2' as \"value\", 'Child' as label")
					.constant().connect(link)
					.fetchJsonTree();
			assertEquals(1, tree.size());
			JsonObject root = (JsonObject) tree.get(0);
			assertEquals("Root", root.getString("label").orElseThrow());
			JsonObject child = (JsonObject) root.getJsonArray("children").orElseThrow().get(0);
			assertEquals("Child", child.getString("label").orElseThrow());
		}
	}

	@Test
	public void shouldFetchPrimitiveValues()
	{
		try (Link link = TestDataSource.getLink())
		{
			assertTrue(Query.of("select true").constant().connect(link).fetchBoolean());
			assertEquals('A', Query.of("select 65").constant().connect(link).fetchChar());
			assertEquals((byte) 7, Query.of("select 7").constant().connect(link).fetchByte());
			assertEquals((short) 8, Query.of("select 8").constant().connect(link).fetchShort());
			assertEquals(9, Query.of("select 9").constant().connect(link).fetchInt());
			assertEquals(10L, Query.of("select 10").constant().connect(link).fetchLong());
			assertEquals(1.5F, Query.of("select 1.5").constant().connect(link).fetchFloat());
			assertEquals(2.5D, Query.of("select 2.5").constant().connect(link).fetchDouble());
		}
	}
}