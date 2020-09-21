/* global customElements */

class AppEvents extends HTMLElement
{
	constructor()
	{
		super();
	}
	
	static listen()
	{
		AppEvents.register(window);
	}

	static register(listener)
	{
		if (AppEvents.connection)
			throw new Error("Attempt to redefine an app event listener");

		let protocol = location.protocol === 'https:' ? "wss://" : "ws://";
		var url = protocol + /.*:\/\/(.*\/.*)\//.exec(location.href)[1] + "/AppEvents";

		AppEvents.connection = new WebSocket(url);

		AppEvents.connection.onmessage = event =>
		{
			event = JSON.parse(event.data);
			listener.dispatchEvent(new CustomEvent(event.type, {detail: event.detail}));
		};

		AppEvents.connection.onopen = () => console.log("listening to app events");
		AppEvents.connection.onclose = () => AppEvents.connection = new WebSocket(url);
	}

	connectedCallback()
	{
		AppEvents.register(this);
	}
};

window.addEventListener("load", () => Array.from(document.querySelectorAll("[data-event-source]"))
		.forEach(listener => AppEvents.register(listener)));

customElements.define('g-event-source', AppEvents);