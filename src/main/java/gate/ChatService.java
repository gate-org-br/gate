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
import gate.sql.update.Update;
import gate.type.ID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/chat")
public class ChatService
{

	@Inject
	private ChatControl control;

	@Inject
	private Event<AppEvent> event;

	@GET
	@Path("/host")
	@Produces("application/json")
	public String host()
	{
		try
		{
			return control.host().toString();
		} catch (NotFoundException ex)
		{
			throw new BadRequestException(Response
				.status(Response.Status.BAD_REQUEST)
				.entity(ex.getMessage())
				.build());
		}
	}

	@GET
	@Path("/peers")
	@Produces("application/json")
	public Object peers()
	{
		return control.peers().toString();
	}

	@GET
	@Produces("application/json")
	@Path("/peers/{peer}/messages")
	public String messages(@PathParam("peer") String peer)
	{
		return control.messages(new ID(peer)).stream()
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
			.collect(Collectors.toCollection(JsonArray::new))
			.toString();
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/peers/{peer}/messages")
	public String post(@PathParam("peer") String peer,
		String message)
	{
		try
		{
			Chat msg = control.post(new ID(peer), message);

			event.fireAsync(new ChatEvent(msg));

			return msg.getId().toString();
		} catch (AppException ex)
		{
			throw new BadRequestException(Response
				.status(Response.Status.BAD_REQUEST)
				.entity(ex.getMessage())
				.build());
		}
	}

	@PATCH
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/peers/{peer}/messages")
	public String received(@PathParam("peer") String peer)
	{
		try
		{
			control.received(new ID(peer));
			return peer;
		} catch (AppException ex)
		{
			throw new BadRequestException(Response
				.status(Response.Status.BAD_REQUEST)
				.type(MediaType.TEXT_PLAIN)
				.entity(ex.getMessage())
				.build());
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

		private ID getHostId() throws ForbiddenException
		{
			var host = getUser();
			if (host == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());
			return host.getId();
		}

		public JsonArray peers()
		{
			try ( PeerDao dao = new PeerDao())
			{
				return dao.search(getHostId());
			}
		}

		public List<Chat> messages(ID peer)
		{
			try ( ChatDao dao = new ChatDao())
			{
				return dao.search(getHostId(), peer);
			}
		}

		public JsonObject host() throws NotFoundException
		{
			try ( PeerDao dao = new PeerDao())
			{
				return dao.select(getHostId());
			}
		}

		public Chat post(ID peer, String message) throws AppException
		{
			try ( UserDao userDao = new UserDao();
				 ChatDao chatDao = new ChatDao())
			{
				var chat = new Chat();
				chat.setSender(getUser());
				chat.setStatus(Chat.Status.POSTED);
				chat.setReceiver(userDao.select(peer));
				chat.setDate(LocalDateTime.now());
				chat.setText(message);
				chatDao.insert(chat);
				return chat;
			}
		}

		public void received(ID peer) throws AppException
		{
			try ( ChatDao dao = new ChatDao())
			{
				var host = getHostId();
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

		public static class UserDao extends Dao
		{

			public UserDao()
			{
				super("Gate");
			}

			public User select(ID id) throws NotFoundException
			{
				return getLink().from("select id, name from gate.Uzer where id = ?")
					.parameters(id)
					.fetchEntity(User.class)
					.orElseThrow(NotFoundException::new);
			}

		}

		public static class PeerDao extends Dao
		{

			public PeerDao()
			{
				super("Gate");
			}

			public JsonObject select(ID id)
				throws NotFoundException
			{
				return getLink().from(getClass().getResource("ChatService/PeerDao/select(ID).sql"))
					.parameters(id)
					.fetchJsonObject()
					.orElseThrow(NotFoundException::new);
			}

			public JsonArray search(ID id)
			{
				return getLink().from(getClass().getResource("ChatService/PeerDao/search(ID).sql"))
					.parameters(id, id)
					.fetchJsonArray();
			}

		}

		public static class ChatDao extends Dao
		{

			public ChatDao()
			{
				super("Gate");
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
