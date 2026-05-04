import Base64 from './base64.js';

const BROADCAST_CHANNEL = new BroadcastChannel("G-EVENT-CHANNEL");

function eventSource(path, listener, log)
{
	const es = new EventSource(window.location.origin + path);

	es.onopen = () =>
	{
		listener.setAttribute("data-connected", "");
		if (log)
			console.log('listening to app events.');
	};

	es.addEventListener('message', (message) =>
	{
		let json = Base64.decode(message.data);
		log && console.log(json);
		const event = JSON.parse(json);
		BROADCAST_CHANNEL.postMessage(event);
		listener.dispatchEvent(new CustomEvent("sse", {bubbles: true, composed: true, detail: event}));
		listener.dispatchEvent(new CustomEvent(event.type, {bubbles: true, composed: true, detail: event.detail}));

	});

	es.onerror = error => log && console.error('Error when listening to app events:', error);


	window.addEventListener('pagehide', e =>
	{
		if (!e.persisted)
		{
			es.close();
			listener.removeAttribute("data-connected");
		}
	});

	return es;
}

function eventTarget(listener)
{
	BROADCAST_CHANNEL.addEventListener("message", (message) =>
	{
		const event = message.data;
		listener.dispatchEvent(new CustomEvent("sse", {bubbles: true, composed: true, detail: event}));
		listener.dispatchEvent(new CustomEvent(event.type, {bubbles: true, composed: true, detail: event.detail}));
	});
}

Array.from(document.querySelectorAll("[data-event-source]"))
	.forEach(listener => eventSource(listener.getAttribute("data-event-source"),
			listener, listener.hasAttribute("data-event-source:log")));

Array.from(document.querySelectorAll("[data-event-target]"))
	.forEach(listener => eventTarget(listener));