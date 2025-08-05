import Base64 from './base64.js';

function eventSource(path, listener, log)
{
	const eventSource = new EventSource(window.location.origin + path);

	eventSource.onopen = () => log && console.log('listening to app events.');

	eventSource.addEventListener('message', (message) =>
	{
		let json = Base64.decode(message.data);
		log && console.log(json);
		const event = JSON.parse(json);
		listener.dispatchEvent(new CustomEvent("sse", {bubbles: true, composed: true, detail: event}));
		listener.dispatchEvent(new CustomEvent(event.type, {bubbles: true, composed: true, detail: event.detail}));
	});

	eventSource.onerror = error => log && console.error('Error when listening to app events:', error);

	window.addEventListener('beforeunload', () => eventSource.close(), {once: true});
}

Array.from(document.querySelectorAll("[data-event-source]"))
	.forEach(listener => eventSource(listener.getAttribute("data-event-source"),
			listener, listener.hasAttribute("data-event-source:log")));