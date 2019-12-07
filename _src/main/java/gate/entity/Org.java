package gate.entity;

import gate.annotation.Description;
import gate.annotation.Entity;
import gate.annotation.Icon;
import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.type.TimeInterval;
import gate.type.collections.StringList;
import gate.type.mime.MimeData;
import java.io.Serializable;

@Icon("2006")
@Entity("orgID")
public class Org implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Required
	@Maxlength(16)
	@Description("Entre com o nome da organização")
	private String orgID;

	@Required
	@Maxlength(64)
	@Description("Entre com a razão social da organização")
	private String name;

	@Maxlength(256)
	@Description("Entre com a descrição da organização")
	private String description;

	@Required
	@Description("Entre com o logo da organização")
	private MimeData icon;

	@Description("Entre com a lista de servidores LDAP que serão utilizados para autenticação")
	private StringList authenticators;

	@Description("Expediente da organização nos domingos")
	private TimeInterval sun;

	@Description("Expediente da organização nas segundas")
	private TimeInterval mon;

	@Description("Expediente da organização nas terças")
	private TimeInterval tue;

	@Description("Expediente da organização nas quartas")
	private TimeInterval wed;

	@Description("Expediente da organização nas quintas")
	private TimeInterval thu;

	@Description("Expediente da organização nas sextas")
	private TimeInterval fri;

	@Description("Expediente da organização nas sábados")
	private TimeInterval sat;

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

	public StringList getAuthenticators()
	{
		if (authenticators == null)
			authenticators = new StringList();
		return authenticators;
	}

	public void setAuthenticators(StringList authenticators)
	{
		this.authenticators = authenticators;
	}

	public TimeInterval getSun()
	{
		return sun;
	}

	public void setSun(TimeInterval sun)
	{
		this.sun = sun;
	}

	public TimeInterval getMon()
	{
		return mon;
	}

	public void setMon(TimeInterval mon)
	{
		this.mon = mon;
	}

	public TimeInterval getTue()
	{
		return tue;
	}

	public void setTue(TimeInterval tue)
	{
		this.tue = tue;
	}

	public TimeInterval getWed()
	{
		return wed;
	}

	public void setWed(TimeInterval wed)
	{
		this.wed = wed;
	}

	public TimeInterval getThu()
	{
		return thu;
	}

	public void setThu(TimeInterval thu)
	{
		this.thu = thu;
	}

	public TimeInterval getFri()
	{
		return fri;
	}

	public void setFri(TimeInterval fri)
	{
		this.fri = fri;
	}

	public TimeInterval getSat()
	{
		return sat;
	}

	public void setSat(TimeInterval sat)
	{
		this.sat = sat;
	}
}
