package mock;

import gate.annotation.Entity;
import gate.sql.annotation.Column;
import gate.sql.annotation.Table;
import gate.type.ID;
import gate.type.LocalDateInterval;

import java.time.LocalDate;

@Entity
@Table("Uzer")
public class UserMock
{
	private int id;
	private String name;
	private Boolean active;
	private RoleMock role;
	private LocalDate birthdate;
	private LocalDateInterval contract;

	public int getId()
	{
		return id;
	}

	public UserMock setId(ID id)
	{
		this.id = id.getValue();
		return this;
	}

	public UserMock setId(int id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public UserMock setName(String name)
	{
		this.name = name;
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

	public Boolean getActive()
	{
		return active;
	}

	public UserMock setActive(Boolean active)
	{
		this.active = active;
		return this;
	}

	@Column("Role$id")
	public RoleMock getRole()
	{
		return role == null ? role = new RoleMock() : role;
	}

	public UserMock setRole(RoleMock role)
	{
		this.role = role;
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
}
