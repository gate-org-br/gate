package mock;

import gate.type.LocalDateInterval;

import java.time.LocalDate;

public class DoctorMock
{
	private int id;
	private String name;
	private LocalDate birthdate;
	private LocalDateInterval contract;

	public int getId()
	{
		return id;
	}

	public DoctorMock setId(int id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public DoctorMock setName(String name)
	{
		this.name = name;
		return this;
	}

	public LocalDate getBirthdate()
	{
		return birthdate;
	}

	public DoctorMock setBirthdate(LocalDate birthdate)
	{
		this.birthdate = birthdate;
		return this;
	}

	public LocalDateInterval getContract()
	{
		return contract;
	}

	public DoctorMock setContract(LocalDateInterval contract)
	{
		this.contract = contract;
		return this;
	}
}