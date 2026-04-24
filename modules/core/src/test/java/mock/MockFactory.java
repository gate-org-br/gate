package mock;

import java.time.LocalDate;
import java.util.List;

public final class MockFactory
{
	private MockFactory()
	{
	}

	public static UserMock user()
	{
		return user(1);
	}

	public static UserMock user(int index)
	{
		return new UserMock()
				.setId(index)
				.setName("User " + index)
				.setEmail(new EmailMock("user%s@example.com".formatted(index)))
				.setLevel(10)
				.setBirthdate(LocalDate.of(2000, 1, 1))
				.setRole(role())
				.setContacts(List.of(
						new ContactMock()
								.setId(1)
								.setType(ContactMock.Type.EMAIL)
								.setValue("user%s@example.com".formatted(index)),
						new ContactMock()
								.setId(2)
								.setType(ContactMock.Type.PHONE)
								.setValue("+55 71 99999-0000")))
				.setDocuments(List.of(
						new DocumentMock("123", DocumentMock.Type.CPF, "SSP"),
						new DocumentMock("AA000000", DocumentMock.Type.PASSPORT, "PF")));
	}

	public static RoleMock role()
	{
		return role(1);
	}

	public static RoleMock role(int index)
	{
		return new RoleMock()
				.setId(IDMock.valueOf(index))
				.setName(index == 1 ? "Role" : "Role " + index)
				.setActive(true)
				.setManager(new UserMock()
						.setId(0)
						.setName("Manager")
						.setLevel(99));
	}
}