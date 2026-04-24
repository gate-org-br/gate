package gate.messaging;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
class Inboxes
{
	@Inject
	InboxConfig config;

	@Inject
	Event<MailEvent> event;

	private List<Inbox> inboxes;

	@PostConstruct
	void init()
	{
		inboxes = config.inbox().stream()
				.map(inbox -> Inbox.of(inbox, event))
				.toList();
	}

	void dispatch()
	{
		inboxes.forEach(Inbox::dispatch);
	}
}
