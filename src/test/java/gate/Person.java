package gate;

import gate.annotation.Entity;
import gate.type.LocalDateInterval;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Person
{

	private int id;
	private String name;
	private LocalDate birthdate;
	private LocalDateInterval contract;
	private transient List<Contact> contacts;

	public int getId()
	{
		return id;
	}

	public Person setId(int id)
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

	public LocalDateInterval getContract()
	{
		return contract;
	}

	public Person setContract(LocalDateInterval contract)
	{
		this.contract = contract;
		return this;
	}
}
