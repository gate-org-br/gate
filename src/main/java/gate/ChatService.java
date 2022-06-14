package gate;

import gate.base.Control;
import gate.base.Dao;
import gate.converter.Converter;
import gate.entity.Chat;
import gate.entity.User;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.event.ChatEvent;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.sql.insert.Insert;
import gate.type.ID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("chat")
public class ChatService
{

	@Inject
	private ChatControl control;

	@Inject
	private Event<ChatEvent> event;

	@GET
	@Path("/users")
	@Produces("text/javascript")
	public String users()
	{
		return control.getUsers()
			.stream()
			.map(e -> new JsonObject()
			.setInt("id", e.getId().getValue())
			.setString("name", e.getName())
			.setString("status", e.getStatus().name()))
			.collect(Collectors.toCollection(JsonArray::new))
			.toString();
	}

	@GET
	@Produces("text/javascript")
	@Path("/post/{user}/{message}")
	public String post(@PathParam("user") String user,
		@PathParam("message") String message)
	{
		try
		{
			Chat msg = control.insert(new ID(user), message);

			event.fireAsync(new ChatEvent(msg));

			return new JsonObject()
				.setString("status", "success")
				.setInt("id", msg.getId().getValue())
				.toString();
		} catch (AppException ex)
		{
			return new JsonObject()
				.setString("status", "error")
				.setString("message", ex.getMessage())
				.toString();
		}
	}

	@GET
	@Path("/messages/{peer}")
	@Produces("text/javascript")
	public String chat(@PathParam("peer") String peer)
	{
		return control.search(new ID(peer))
			.stream()
			.map(e -> new JsonObject()
			.setInt("id", e.getId().getValue())
			.set("sender", new JsonObject()
				.setInt("id", e.getSender().getId().getValue())
				.setString("name", e.getSender().getName()))
			.set("receiver", new JsonObject()
				.setInt("id", e.getReceiver().getId().getValue())
				.setString("name", e.getReceiver().getName()))
			.setString("text", e.getText())
			.setString("date", Converter.toText(e.getDate())))
			.collect(Collectors.toCollection(JsonArray::new))
			.toString();
	}

	@Dependent
	public static class ChatControl extends Control
	{

		public List<User> getUsers()
		{
			try ( ChatDao dao = new ChatDao())
			{
				return dao.getUsers();
			}
		}

		public Chat insert(ID user, String message)
			throws AppException
		{
			var chat = new Chat();
			chat.setSender(getUser());
			if (chat.getSender().getId() == null)
				throw new AppException("Tentativa de enviar mensagem sem estar autenticado");

			try ( ChatDao dao = new ChatDao())
			{
				chat.setReceiver(dao.getUser(user));
				chat.setDate(LocalDateTime.now());
				chat.setText(message);
				dao.insert(chat);
				return chat;
			}
		}

		public List<Chat> search(ID peer)
		{
			try ( ChatDao dao = new ChatDao())
			{
				return dao.search(getUser().getId(), peer);
			}
		}

		public static class ChatDao extends Dao
		{

			public ChatDao()
			{
				super("Gate");
			}

			public User getUser(ID id) throws NotFoundException
			{
				return getLink().from("select id, name from Uzer where id = ?")
					.parameters(id)
					.fetchEntity(User.class)
					.orElseThrow(() -> new NotFoundException("Destinatário inválido"));
			}

			public List<User> getUsers()
			{
				return getLink().from("select id, status, name from Uzer where status <> 'INACTIVE' order by name")
					.constant()
					.fetchEntityList(User.class);
			}

			public void insert(Chat chat) throws ConstraintViolationException
			{
				Insert.into("Chat")
					.set("Sender$id", chat.getSender().getId())
					.set("Receiver$id", chat.getReceiver().getId())
					.set("date", chat.getDate())
					.set("text", chat.getText())
					.build()
					.connect(getLink())
					.fetchGeneratedKey(ID.class)
					.ifPresent(chat::setId);
			}

			public List<Chat> search(ID host, ID peer)
			{
				return getLink().from(getClass().getResource("Chat/serch(ID, ID).sql"))
					.parameters(host, peer, host, peer)
					.fetchEntityList(Chat.class);
			}
		}
	}
}
