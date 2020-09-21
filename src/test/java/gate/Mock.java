package gate;

import gate.annotation.Entity;
import gate.constraint.Max;
import gate.constraint.Maxlength;
import gate.constraint.Min;
import gate.constraint.Pattern;
import gate.constraint.Required;
import gate.constraint.Step;
import gate.type.ID;

/**
 *
 * @author davins
 */
@Entity
public class Mock
{

	@Required
	private ID id;

	@Required
	@Maxlength(4)
	@Pattern("^P[0-9]$")
	private String name;

	@Min(2)
	@Max(10)
	@Step("2")
	@Required
	private Integer age;

	public ID getId()
	{
		return id;
	}

	public void setId(ID id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getAge()
	{
		return age;
	}

	public void setAge(Integer age)
	{
		this.age = age;
	}

}
