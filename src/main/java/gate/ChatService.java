package gate;

import gate.base.Control;
import gate.base.Dao;
import gate.converter.Converter;
import gate.entity.Chat;
import gate.entity.User;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.event.AppEvent;
import gate.event.ChatEvent;
import gate.event.ChatReceivedEvent;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.sql.condition.Condition;
import gate.sql.insert.Insert;
import gate.sql.select.Select;
import gate.sql.update.Update;
import gate.type.ID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/chat")
public class ChatService
{

	@Inject
	private ChatControl control;

	@Inject
	private Event<AppEvent> event;

	@GET
	@Path("/peers")
	@Produces("application/json")
	public String peers()
	{
		try
		{
			return new JsonObject()
				.setString("status", "success")
				.set("peers", control.getPeers())
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
	@Produces("application/json")
	@Path("/post/{peer}/{message}")
	public String post(@PathParam("peer") String peer,
		@PathParam("message") String message)
	{
		try
		{
			Chat msg = control.post(new ID(peer), message);

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
	@Path("/summary")
	@Produces("application/json")
	public String summary()
	{
		try
		{
			return new JsonObject()
				.setString("status", "success")
				.set("result", control.summary())
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
	@Path("/summary/{peer}")
	@Produces("application/json")
	public String summary(@PathParam("peer") String peer)
	{
		try
		{
			return new JsonObject()
				.setString("status", "success")
				.set("result", control.summary(new ID(peer)))
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
	@Produces("application/json")
	public String messages(@PathParam("peer") String peer)
	{
		try
		{
			var messges = control.getMessages(new ID(peer));
			var result = messges.stream()
				.map(e -> new JsonObject()
				.setInt("id", e.getId().getValue())
				.set("sender", new JsonObject()
					.setInt("id", e.getSender().getId().getValue())
					.setString("name", e.getSender().getName()))
				.set("receiver", new JsonObject()
					.setInt("id", e.getReceiver().getId().getValue())
					.setString("name", e.getReceiver().getName()))
				.setString("text", e.getText())
				.setString("status", e.getStatus().name())
				.setString("date", Converter.toText(e.getDate())))
				.collect(Collectors.toCollection(JsonArray::new));

			return new JsonObject()
				.setString("status", "success")
				.set("messages", result)
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
	@Path("/received/{peer}")
	@Produces("application/json")
	public String received(@PathParam("peer") String peer)
	{
		try
		{
			control.received(new ID(peer));
			return new JsonObject()
				.setString("status", "success")
				.toString();
		} catch (AppException ex)
		{
			return new JsonObject()
				.setString("status", "error")
				.setString("message", ex.getMessage())
				.toString();
		}
	}

	public void post(String message, List<? extends User> peers) throws AppException
	{
		control.post(message, peers)
			.stream().map(ChatEvent::new)
			.forEach(event::fireAsync);
	}

	@Dependent
	public static class ChatControl extends Control
	{

		@Inject
		private Event<AppEvent> event;

		private ID host() throws AppException
		{
			var host = getUser();
			if (host == null)
				throw new AppException("Tentativa de utilizar chat sem estar logado");
			return host.getId();
		}

		public JsonObject summary() throws AppException
		{
			try ( ChatDao dao = new ChatDao())
			{
				return dao.summary(null, host());
			}
		}

		public JsonObject summary(ID peer) throws AppException
		{
			try ( ChatDao dao = new ChatDao())
			{
				return dao.summary(peer, host());
			}
		}

		public JsonArray getPeers() throws AppException
		{
			try ( PeerDao dao = new PeerDao())
			{
				return dao.search(host());
			}
		}

		public Chat post(ID peer, String message) throws AppException
		{
			try ( PeerDao peerDao = new PeerDao();
				 ChatDao chatDao = new ChatDao())
			{
				var chat = new Chat();
				chat.setSender(getUser());
				chat.setStatus(Chat.Status.POSTED);
				chat.setReceiver(peerDao.select(peer));
				chat.setDate(LocalDateTime.now());
				chat.setText(message);
				chatDao.insert(chat);
				return chat;
			}
		}

		public List<Chat> getMessages(ID peer) throws AppException
		{
			try ( ChatDao dao = new ChatDao())
			{
				return dao.search(host(), peer);
			}
		}

		public void received(ID peer) throws AppException
		{
			try ( ChatDao dao = new ChatDao())
			{
				var host = host();
				dao.update(peer, host, Chat.Status.RECEIVED);
				event.fireAsync(new ChatReceivedEvent(peer, host));
			}
		}

		public List<Chat> post(String message, List<? extends User> peers) throws AppException
		{
			try ( ChatDao dao = new ChatDao())
			{
				dao.beginTran();
				var messages = new ArrayList<Chat>();
				for (User peer : peers)
				{
					var chat = new Chat();
					chat.setText(message);
					chat.setReceiver(peer);
					chat.setSender(getUser());
					chat.setDate(LocalDateTime.now());
					chat.setStatus(Chat.Status.POSTED);
					dao.insert(chat);
					messages.add(chat);
				}
				dao.commit();
				return messages;
			}
		}

		public static class PeerDao extends Dao
		{

			public PeerDao()
			{
				super("Gate");
			}

			public JsonArray search(ID id)
			{
				return getLink().from(getClass().getResource("ChatService/PeerDao/search(ID).sql"))
					.parameters(id, id)
					.fetchJsonArray();
			}

			public User select(ID id) throws NotFoundException
			{
				return getLink().from("select id, name from gate.Uzer where id = ?")
					.parameters(id)
					.fetchEntity(User.class)
					.orElseThrow(NotFoundException::new);
			}
		}

		public static class ChatDao extends Dao
		{

			public ChatDao()
			{
				super("Gate");
			}

			public JsonObject summary(ID sender, ID receiver)
				throws NotFoundException
			{
				return Select.expression("count(*)").as("messages")
					.expression("coalesce(sum(status='POSTED'), 0)").as("posted")
					.expression("coalesce(sum(status='RECEIVED'), 0)").as("received")
					.from("gate.Chat")
					.where(Condition
						.of("Sender$id").eq(sender)
						.and("Receiver$id").eq(receiver))
					.build()
					.connect(getLink())
					.fetchJsonObject()
					.orElseThrow(NotFoundException::new);
			}

			public List<Chat> search(ID host, ID peer)
			{
				return getLink().from(getClass().getResource("ChatService/ChatDao/search(ID, ID).sql"))
					.parameters(host, peer, host, peer)
					.fetchEntityList(Chat.class);
			}

			public void insert(Chat chat) throws ConstraintViolationException
			{
				Insert.into("Chat")
					.set("Sender$id", chat.getSender().getId())
					.set("Receiver$id", chat.getReceiver().getId())
					.set("date", chat.getDate())
					.set("text", chat.getText())
					.set("status", chat.getStatus())
					.build()
					.connect(getLink())
					.fetchGeneratedKey(ID.class)
					.ifPresent(chat::setId);
			}

			public void update(ID sender, ID receiver, Chat.Status status) throws ConstraintViolationException
			{
				Update.table("Chat")
					.set("status", status)
					.where(Condition.of("Sender$id").eq(sender)
						.and("Receiver$id").eq(receiver))
					.build()
					.connect(getLink())
					.execute();
			}
		}
	}
}
