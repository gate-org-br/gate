package gate.entity;

import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.type.LocalTimeInterval;
import gate.type.mime.MimeData;

import java.io.Serial;
import java.io.Serializable;

@Icon("2006")
@Entity("orgID")
@Name
public class Org implements Serializable
{

	@Serial private static final long serialVersionUID = 1L;

	@Required
	@Maxlength(16)
	@Description
	private String orgID;

	@Required
	@Maxlength(64)
	@Description
	private String name;

	@Maxlength(256)
	@Description
	private String description;

	@Required
	@Description
	private MimeData icon;

	@Description
	private LocalTimeInterval sun;

	@Description
	private LocalTimeInterval mon;

	@Description
	private LocalTimeInterval tue;

	@Description
	private LocalTimeInterval wed;

	@Description
	private LocalTimeInterval thu;

	@Description
	private LocalTimeInterval fri;

	@Description
	private LocalTimeInterval sat;

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getOrgID()
	{
		return orgID;
	}

	public Org setOrgID(String orgID)
	{
		this.orgID = orgID;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Org setName(String name)
	{
		this.name = name;
		return this;
	}

	public MimeData getIcon()
	{
		return icon;
	}

	public Org setIcon(MimeData icon)
	{
		this.icon = icon;
		return this;
	}

	public LocalTimeInterval getSun()
	{
		return sun;
	}

	public void setSun(LocalTimeInterval sun)
	{
		this.sun = sun;
	}

	public LocalTimeInterval getMon()
	{
		return mon;
	}

	public void setMon(LocalTimeInterval mon)
	{
		this.mon = mon;
	}

	public LocalTimeInterval getTue()
	{
		return tue;
	}

	public void setTue(LocalTimeInterval tue)
	{
		this.tue = tue;
	}

	public LocalTimeInterval getWed()
	{
		return wed;
	}

	public void setWed(LocalTimeInterval wed)
	{
		this.wed = wed;
	}

	public LocalTimeInterval getThu()
	{
		return thu;
	}

	public void setThu(LocalTimeInterval thu)
	{
		this.thu = thu;
	}

	public LocalTimeInterval getFri()
	{
		return fri;
	}

	public void setFri(LocalTimeInterval fri)
	{
		this.fri = fri;
	}

	public LocalTimeInterval getSat()
	{
		return sat;
	}

	public void setSat(LocalTimeInterval sat)
	{
		this.sat = sat;
	}
}