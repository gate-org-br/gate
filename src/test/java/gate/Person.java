package gate;

import gate.annotation.Entity;
import gate.type.DateInterval;
import gate.type.ID;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Person
{

	private ID id;
	private String name;
	private LocalDate birthdate;
	private DateInterval contract;
	private transient List<Contact> contacts;

	public ID getId()
	{
		return id;
	}

	public Person setId(ID id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Person setName(String name)
	{
		this.name = name;
		return this;
	}

	public LocalDate getBirthdate()
	{
		return birthdate;
	}

	public Person setBirthdate(LocalDate birthdate)
	{
		this.birthdate = birthdate;
		return this;
	}

	public List<Contact> getContacts()
	{
		if (contacts == null)
			contacts = new ArrayList<>();
		return contacts;
	}

	public void setContacts(List<Contact> contatos)
	{
		this.contacts = contatos;
	}

	public Person addContact(Contact contact)
	{
		getContacts().add(contact);
		contact.setPerson(this);
		return this;
	}

	public DateInterval getContract()
	{
		return contract;
	}

	public Person setContract(DateInterval contract)
	{
		this.contract = contract;
		return this;
	}
}
