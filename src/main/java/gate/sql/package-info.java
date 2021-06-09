/**
 * Provides a clean interface to execute statements and queries on databases and to fetch
 * java objects from them.
 * <hr>
 * Creating a {@link gate.sql.Link} to a database using a JDBC connection or a data source name:
 * <pre>{@code
 * try (Link link = new Link(jdbcConnection))
 * try (Link link = new Link("datasourcename"))
 * }</pre>
 * <hr>
 * Executing a hard coded SQL statement:
 * <pre>{@code
 * try (Link link = new Link("datasourcename"))
 * {
 *	link
 *		.prepare("insert into Table (field1, field2) values (?, ?)")
 *		.parameters(parameter1, parameter2)
 *		.execute();
 * }
 * }</pre>
 * <hr>
 * Executing a SQL statement loaded from a resource file:
 * <pre>{@code
 * try (Link link = new Link("datasourcename"))
 * {
 *	link
 *		.prepare(getClass().getResource("InsertStatement.sql"))
 *		.parameters(parameter1, parameter2)
 *		.execute();
 * }
 * }</pre>
 * <hr>
 * Executing hard coded SQL queries and fetching results on various formats:
 * <pre>{@code
 * try (Link link = new Link("datasourcename"))
 * {
 *		Integer count = link
 *		.from("select count(*) from User where Role$id = ?")
 *			.parameters(new ID(1))
 *			.fetchObject(Integer.class)
 *			.get();
 *
 *		Query.Connected.Compiled query = link
 *		.from("select id, name from User where Role$id = ?")
 *		.parameters(new ID(1));
 *
 *		List<User> users = query.fetchEntityList(User.class);
 *		for (User user : users)
 *			System.out.print("id: " + user.getId() +
 *				" name: " + user.getName());
 *
 *		List<Object[]> arrays
 *			= query.fetchArrayList(ID.class, String.class);
 *		for (Object[] array : arrays)
 *			System.out.print("id: " + array[0]
 *				+ " name: " + array[1]);
 *
 *		List<Map<String, Object>> maps
 *			= query.fetchMapList(ID.class, String.class);
 *		for (Map<String, Object> map : maps)
 *			System.out.print("id: " + map.get("id") +
 *				" name: " + map.get("name"));
 * }
 * }</pre>
 * <hr>
 * Building SQL sentences using the provided sentence Builders:
 * <pre>{@code
 * try (Link link = new Link("datasourcename"))
 * {
 *	link.prepare(Insert
 *		.into("Person")
 *		.set(ID.class, "id", new ID(1))
 *		.set(String.class, "name", "Jonh")
 *		.set(Date.class, "birthdate", new Date('01/01/2000'))
 *	.execute();
 *
 *	link.prepare(Update
 *		.table("Uzer")
 * 		.set("name", "Paul")
 * 		.where(Condition.of("id").eq(new ID(1))))
 *		.execute();
 *
 *	link.prepare(Delete
 *		.from("Uzer")
 * 		.where(Condition.of("id").eq()))
 *		.parameters(new ID(1))
 *		.execute();
 * }
 * }</pre>
 * <hr>
 * Building SQL sentences from java types:
 * <pre>{@code
 * try (Link link = new Link("datasourcename"))
 * {
 *	Person person = new Person();
 *	person.setId(new ID(1));
 *	person.setName("Jonh");
 *
 *	link.prepare(Insert
 *		.into(Person.class)
 *		.set("id", "name"))
 *		.value(person)
 *		.execute();
 *
 *	person.setName("Richard");
 *	person.setBirthDate(new Date("02/01/2000"));
 *  	link.prepare(Update
 *		.type(Person.class)
 *		.value(person)
 *		.execute();
 *
 *  	link.prepare(Delete
 *		.from(Person.class)
 *		.value(person)
 *		.execute();
 * }
 * }</pre>
 *
 */
package gate.sql;
