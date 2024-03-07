let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-event-source">:host(*) { display: none }</style>`;
/* global customElements, template */

export default class GEventSource extends HTMLElement
{

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		GEventSource.register(this, this.log);
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get log()
	{
		return this.hasAttribute("log");
	}

	set log(value)
	{
		if (value)
			this.setAttribute("log", "");
		else
			this.removeAttribute("log");
	}

	static register(listener, log)
	{
		const eventSource = new EventSource(`${window.location.origin}/SSE`);

		eventSource.onopen = () => log && console.log('listening to app events.');

		eventSource.addEventListener('message', (message) =>
		{
			let json = atob(message.data);
			log && console.log(json);
			const event = JSON.parse(json);
			listener.dispatchEvent(new CustomEvent("sse", {bubbles: true, composed: true, detail: event}));
			listener.dispatchEvent(new CustomEvent(event.type, {bubbles: true, composed: true, detail: event.details}));
		});

		eventSource.onerror = error => log && console.error('Error when listening to app events:', error);

		window.addEventListener('beforeunload', () => eventSource.close(), {once: true});
	}

}


customElements.define('g-event-source', GEventSource);

Array.from(document.querySelectorAll("[data-event-source]"))
	.forEach(listener => GEventSource.register(listener, listener.hasAttribute("data-event-source:log")));