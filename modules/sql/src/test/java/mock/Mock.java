package mock;

import gate.annotation.Entity;
import gate.type.ID;

@Entity
public class Mock
{
	private ID id;

	public ID getId()
	{
		return id;
	}

	public Mock setId(ID id)
	{
		this.id = id;
		return this;
	}
}
