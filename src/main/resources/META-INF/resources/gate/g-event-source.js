let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*) { display: none }</style>`;

/* global customElements */

let connection;

export default function registerEventSource(listener)
{
	if (connection)
		throw new Error("Attempt to redefine an app event listener");

	let protocol = location.protocol === 'https:' ? "wss://" : "ws://";
	let hostname = location.hostname;
	let port = location.port ? ':' + location.port : '';
	let pathname = location.pathname.replace(/\/Gate.*/, "") + "/AppEvents";
	let url = protocol + hostname + port + pathname;

	function connect()
	{
		connection = new WebSocket(url);
		
		connection.onmessage = event =>
		{
			event = JSON.parse(event.data);
			listener.dispatchEvent(new CustomEvent(event.type, {detail: event.detail, composed: true}));
		};
		
		connection.onopen = () =>
		{
			console.log("listening to app events");
			listener.dispatchEvent(new CustomEvent('Connected', {composed: true}));
		}
		
		connection.onclose = () =>
		{
			listener.dispatchEvent(new CustomEvent('Disconnected', {composed: true}));
			connect();
		};
	}

	connect();
}

customElements.define('g-event-source', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	connectedCallback()
	{
		registerEventSource(this.hasAttribute("target")
				? document.getElementById(this.getAttribute("target"))
				: window);
	}
});

Array.from(document.querySelectorAll("[data-event-source]")).forEach(listener => registerEventSource(listener));