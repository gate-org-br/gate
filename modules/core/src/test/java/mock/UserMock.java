package mock;

import gate.annotation.Description;
import gate.annotation.Name;
import gate.annotation.NullSafe;
import gate.type.LocalDateInterval;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Name("Usuário")
public class UserMock
{
	private int id;
	private String name;
	@Name("Login do Usuário")
	@Description("O campo LOGIN deve possuir no máximo 64 caracteres.")
	private String username;
	private EmailMock email;
	private Boolean active;
	private int level;
	private LocalDate birthdate;
	private LocalDateInterval contract;
	private RoleMock role;
	private List<ContactMock> contacts;
	private List<DocumentMock> documents;

	public int getId() {return id;}

	public UserMock setId(int id)
	{
		this.id = id;
		return this;
	}

	public String getName() {return name;}

	public UserMock setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getUsername()
	{
		return username;
	}

	public UserMock setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public EmailMock getEmail()
	{
		return email;
	}

	public UserMock setEmail(EmailMock email)
	{
		this.email = email;
		return this;
	}

	public Boolean getActive()
	{
		return active;
	}

	public UserMock setActive(Boolean active)
	{
		this.active = active;
		return this;
	}

	public LocalDate getBirthdate()
	{
		return birthdate;
	}

	public UserMock setBirthdate(LocalDate birthdate)
	{
		this.birthdate = birthdate;
		return this;
	}

	public LocalDateInterval getContract()
	{
		return contract;
	}

	public UserMock setContract(LocalDateInterval contract)
	{
		this.contract = contract;
		return this;
	}

	@NullSafe
	public RoleMock getRole() {return role == null ? role = new RoleMock() : role;}

	public UserMock setRole(RoleMock role)
	{
		this.role = role;
		return this;
	}


	public List<ContactMock> getContacts() {return contacts == null ? contacts = new ArrayList<>() : contacts;}

	public UserMock setContacts(List<ContactMock> contacts)
	{
		this.contacts = contacts;
		return this;
	}

	public UserMock addContact(ContactMock contact)
	{
		getContacts().add(contact);
		contact.setUser(this);
		return this;
	}

	public List<DocumentMock> getDocuments()
	{
		return documents == null ? documents = new ArrayList<>() : documents;
	}

	public UserMock setDocuments(List<DocumentMock> documents)
	{
		this.documents = documents;
		return this;
	}

	public int getLevel() {return level;}

	public UserMock setLevel(int level)
	{
		this.level = level;
		return this;
	}

	public boolean checkAccess(String module, String screen, String action)
	{
		return false;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof UserMock user
		       && id == user.id
		       && level == user.level
		       && Objects.equals(name, user.name)
		       && Objects.equals(username, user.username)
		       && Objects.equals(email, user.email)
		       && Objects.equals(active, user.active)
		       && Objects.equals(birthdate, user.birthdate)
		       && Objects.equals(contract, user.contract)
		       && Objects.equals(contacts, user.contacts)
		       && Objects.equals(documents, user.documents);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, username, email, active, level, birthdate, contract, contacts, documents);
	}
}