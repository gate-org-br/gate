let template = document.createElement("template");
template.innerHTML = `
	<header>
		<i></i>
		<label></label>
		<input id='search' placeholder="Localizar"/>
	</header>
	<section>
		<g-chat-message-list>
		</g-chat-message-list>
	</section>
	<footer>
		<input id='message' type='text' placeholder='Mensagem'/>
		<button>&#X2034</button>
	</footer>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: grid;
	flex: 1 1 100%;
	border-radius: 5px;
	align-items: stretch;
	grid-template-rows: 40px 1fr 40px;
	background-color: var(--table-head-background-color);
	border: 2px solid var(--table-head-background-color);
}

section
{
	display: flex;
	overflow: scroll;
	align-items: stretch;
	background-color: white;
	background-image: var(--noise);
}

g-chat-message-list
{
	flex-grow: 1;
}

header
{
	padding: 8px;
	display: grid;
	flex-basis: 50px;
	align-items: center;
	grid-template-columns: 32px 1fr 128px;
	background-color: var(--table-head-background-color);
}

header > i {
	font-size: 16px;
	flex-basis: 32px;
	font-family: gate;
	font-style: normal;
}

header > label
{
	font-weight: bold;
	border-radius: 5px 0 0 0;
}

:host([status=ONLINE]) > header  > i
{
	color: #006600;
}

:host([status=OFFLINE]) > header > i
{
	color: #660000;
}

footer
{
	display: grid;
	align-items: stretch;
	justify-content: center;
	grid-template-columns: 1fr 48px;
}

input
{
	padding: 4px;
	border: none;
	outline: none;
	background-color: var(--hovered);
}

#search
{
	border-radius: 5px;
}

#message
{
	border-radius: 0 0 0 5px;
}

button
{
	border: none;
	cursor: pointer;
	font-family: gate;
	border-radius: 0 0 5px 0;
}</style>`;

/* global customElements */

import './g-chat-message-list.mjs';
import Message from './g-message.mjs';
import ChatService from './g-chat-service.mjs';

customElements.define('g-chat-contact', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let button = this.shadowRoot.querySelector("button");
		let message = this.shadowRoot.getElementById("message");
		const post = () =>
		{
			message.enabled = false;
			ChatService.post(this.peerId, message.value)
				.then(response =>
				{
					if (response.status === 'error')
						Message.error(response.message);
					message.value = "";
					message.enabled = true;
				});
		}

		button.addEventListener("click", () => post());
		message.addEventListener("keydown", event => event.keyCode === 13 && post());


		let search = this.shadowRoot.getElementById("search");
		let messages = this.shadowRoot.querySelector("g-chat-message-list");

		search.addEventListener("input", () => messages.filter = search.value);
		messages.addEventListener("selected", () => search.value = "");
	}

	set hostId(hostId)
	{
		this.setAttribute("host-id", hostId);
	}

	get hostId()
	{
		return Number(this.getAttribute("host-id"));
	}

	set hostName(hostName)
	{
		this.setAttribute("host-name", hostName);
	}

	get hostName()
	{
		return this.getAttribute("host-name");
	}

	set peerId(peerId)
	{
		this.setAttribute("peer-id", peerId);
	}

	get peerId()
	{
		return Number(this.getAttribute("peer-id"));
	}

	set peerName(peerName)
	{
		this.setAttribute("peer-name", peerName);
	}

	get peerName()
	{
		return this.getAttribute("peer-name");
	}

	set status(status)
	{
		this.setAttribute("status", status);
	}

	get status()
	{
		return this.getAttribute("status");
	}

	connectedCallback()
	{
		this.shadowRoot.querySelector("label").innerText = this.peerName;
		this.shadowRoot.querySelector("i").innerHTML = this.status === 'ONLINE' ? '&#X2004' : '&#X2008';
	}
});
