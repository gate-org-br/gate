package gate;

import gate.ChatService.PeerControl.PeerDao;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@WebServlet(value = "/gate/chat/*")
public class ChatService extends HttpServlet
{

	@Inject
	private HostControl hostControl;

	@Inject
	private PeerControl peerControl;

	@Inject
	private ChatControl chatControl;

	@Inject
	private Event<AppEvent> event;

	@Override
	public void service(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			if ("/host".equals(request.getPathInfo()))
			{
				send(response, 200, "application/json", hostControl.select());
			} else if ("/peers".equals(request.getPathInfo()))
			{
				send(response, 200, "application/json", peerControl.search());
			} else if (request.getPathInfo() != null)
			{
				Matcher matcher = Pattern.compile("^\\/peers\\/([0-9]+)$").matcher(request.getPathInfo());
				if (matcher.matches())
				{
					ID peer = ID.valueOf(matcher.group(1));
					send(response, 200, "application/json", peerControl.select(peer));
				} else
				{
					matcher = Pattern.compile("^\\/peers\\/([0-9]+)/messages$").matcher(request.getPathInfo());
					if (matcher.matches())
					{
						ID peer = ID.valueOf(matcher.group(1));
						switch (request.getMethod().toUpperCase())
						{
							case "GET":
								send(response, 200, "application/json",
									chatControl.search(peer).stream()
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
										.collect(Collectors.toCollection(JsonArray::new)).toString());
								break;

							case "POST":
								StringBuilder message = new StringBuilder();
								try (Reader reader = request.getReader())
								{
									for (int c = reader.read(); c != -1; c = reader.read())
										message.append((char) c);
								}

								if (message.length() > 256)
									throw new AppException("Mensagens devem possuir no máximo 256 caracteres");

								send(response, 200, "text/plain",
									chatControl.insert(peer,
										message.toString()).toString());
								break;
							case "PATCH":
								chatControl.update(peer);
								send(response, 200, "text/plain", matcher.group(1));
								break;
						}
					}
				}
			}
		} catch (NotFoundException ex)
		{
			response.setStatus(404);
		} catch (AppException ex)
		{
			response.setStatus(400);
		}

	}

	private void send(HttpServletResponse response,
		int status, String type, Object result) throws IOException
	{
		response.setStatus(status);
		response.setContentType(type);
		response.setCharacterEncoding("UTF-8");
		try (PrintWriter writer = response.getWriter())
		{
			writer.write(result.toString());
		}
	}

	public void post(String message, List<? extends User> peers) throws AppException
	{
		chatControl.post(message, peers)
			.stream().map(ChatEvent::new)
			.forEach(event::fireAsync);
	}

	@Dependent
	public static class HostControl extends Control
	{

		@Inject
		private Status status;

		public JsonObject select() throws NotFoundException
		{
			if (getUser() == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());

			try (HostDao dao = new HostDao())
			{
				var result = dao.select(getUser());
				result.setString("status", status
					.get(ID.valueOf(result.getInt("id")
						.orElseThrow())).name());
				return result;
			}
		}

		public static class HostDao extends Dao
		{

			public HostDao()
			{
				super("Gate");
			}

			public JsonObject select(User host)
				throws NotFoundException
			{
				return getLink().from(getClass().getResource("ChatService/HostDao/select(ID).sql"))
					.parameters(host.getId())
					.fetchJsonObject()
					.orElseThrow(NotFoundException::new);
			}
		}
	}

	@Dependent
	public static class PeerControl extends Control
	{

		@Inject
		private Status status;

		public JsonObject select(ID id) throws NotFoundException
		{
			if (getUser() == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());

			try (PeerDao dao = new PeerDao())
			{
				var peer = dao.select(getUser(), id);
				peer.setString("status", status
					.get(ID.valueOf(peer.getInt("id")
						.orElseThrow())).name());
				return peer;
			}
		}

		public JsonArray search()
		{
			if (getUser() == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());

			try (PeerDao dao = new PeerDao())
			{
				JsonArray peers = dao.search(getUser());
				peers.stream().map(e -> (JsonObject) e)
					.forEach(e -> e.setString("status",
					status.get(ID.valueOf(e.getInt("id")
						.orElseThrow())).name()));
				return peers;
			}
		}

		public static class PeerDao extends Dao
		{

			public PeerDao()
			{
				super("Gate");
			}

			public JsonArray search(User host)
			{
				return getLink().from(getClass().getResource("ChatService/PeerDao/search(ID).sql"))
					.parameters(host.getId(), host.getId())
					.fetchJsonArray();
			}

			public JsonObject select(User host, ID peer) throws NotFoundException
			{
				return getLink().from(getClass().getResource("ChatService/PeerDao/select(ID, ID).sql"))
					.parameters(host.getId(), peer)
					.fetchJsonObject()
					.orElseThrow(NotFoundException::new);
			}

		}
	}

	@Dependent
	public static class ChatControl extends Control
	{

		@Inject
		private Event<AppEvent> event;

		public List<Chat> search(ID peer)
		{
			if (getUser() == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());

			try (ChatDao dao = new ChatDao())
			{
				return dao.search(peer);
			}
		}

		public Chat insert(ID peer, String message) throws AppException
		{
			if (getUser() == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());

			try (UserDao userDao = new UserDao();
				ChatDao chatDao = new ChatDao())
			{
				var chat = new Chat();
				chat.setSender(getUser());
				chat.setStatus(Chat.Status.POSTED);
				chat.setReceiver(userDao.select(peer));
				chat.setDate(LocalDateTime.now());
				chat.setText(message);
				chatDao.insert(chat);

				event.fireAsync(new ChatEvent(chat));
				return chat;
			}
		}

		public void update(ID peer) throws AppException
		{
			if (getUser() == null)
				throw new ForbiddenException(Response
					.status(Response.Status.FORBIDDEN)
					.entity("Tentativa de acessar chat sem estar logado")
					.type(MediaType.TEXT_PLAIN)
					.build());

			try (ChatDao dao = new ChatDao())
			{
				dao.update(peer, Chat.Status.RECEIVED);
				event.fireAsync(new ChatReceivedEvent(peer, getUser().getId()));
			}
		}

		public List<Chat> post(String message, List<? extends User> peers) throws AppException
		{
			try (ChatDao dao = new ChatDao())
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

		public static class ChatDao extends Dao
		{

			public ChatDao()
			{
				super("Gate");
			}

			public List<Chat> search(ID peer)
			{
				return getLink().from(getClass().getResource("ChatService/ChatDao/search(ID).sql"))
					.parameters(getUser().getId(), peer, getUser().getId(), peer)
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

			public void update(ID peer, Chat.Status status) throws ConstraintViolationException
			{
				Update.table("Chat")
					.set("status", status)
					.where(Condition.of("Sender$id").eq(peer)
						.and("Receiver$id").eq(getUser().getId()))
					.build()
					.connect(getLink())
					.execute();
			}
		}
	}
}
