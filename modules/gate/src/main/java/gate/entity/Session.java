package gate.entity;

import gate.annotation.Entity;
import gate.type.ID;

import java.time.LocalDateTime;

@Entity
public class Session
{
	private ID id;
	private User user;
	private LocalDateTime date;

	public User getUser() {return user == null ? user = new User() : user;}

	public Session setUser(User user)
	{
		this.user = user;
		return this;
	}

	public LocalDateTime getDate() {return date;}

	public Session setDate(LocalDateTime date)
	{
		this.date = date;
		return this;
	}

	public ID getId() {return id;}

	public Session setId(ID id)
	{
		this.id = id;
		return this;
	}
}
