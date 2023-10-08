let template = document.createElement("template");
template.innerHTML = `
	<input type="text" placeholder="Localizar"/>
	<div>
	</div>
	<footer>
		<g-chat-notification-config label="Notificações">
		</g-chat-notification-config>
	</footer>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: grid;
	height: inherit;
	border-radius: 3px;
	align-items: stretch;
	border: 1px solid var(--main6);
	grid-template-rows: 40px 1fr 40px;
}

div
{
	flex-grow: 1;
	display: flex;
	overflow: auto;
	flex-direction: column;
	background-color: white;
	border-top: 1px solid var(--main6);
	border-bottom: 1px solid var(--main6);
}

footer
{
	display: flex;
	align-items: stretch;
	justify-content: center;
	border-radius: 0 0 5px 5px;
}

g-chat-notification-config
{
	flex: 1 1 100%;
}

input
{
	border: none;
	outline: none;
	text-indent: 4px;
}

g-chat-peer:hover
{
	background-color: var(--hovered);
}</style>`;

/* global customElements */

import './g-chat-peer.js';
import './g-chat-notification-config.js';
import GChatService from './g-chat-service.js';
import GMessageDialog from './g-message-dialog.js';

customElements.define('g-chat-peer-list', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let div = this.shadowRoot.querySelector("div");
		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () =>
		{
			let value = input.value.toUpperCase();
			Array.from(div.children)
				.forEach(peer => peer.style.display = !value
						|| peer.peerName.toUpperCase()
						.indexOf(value.toUpperCase()) !== -1 ? "" : "none");
		});

		this.addEventListener("selected", () =>
		{
			input.value = "";
			Array.from(div.children).forEach(row =>
			{
				row.style.display = "";
				row.style.backgoundColor = "";
			});
		});
	}

	get hostId()
	{
		return this.getRootNode().host.hostId;
	}

	select(peer)
	{
		setTimeout(() =>
		{
			peer = Number(peer);
			let div = this.shadowRoot.querySelector("div");
			Array.from(div.children)
				.filter(e => e.peerId === peer)
				.forEach(e => e.click());
		}, 500);
	}

	connectedCallback()
	{
		let root = this.getRootNode().host;
		let div = this.shadowRoot.querySelector("div");

		if (!root.children.length)
		{
			GChatService.peers().then(response =>
			{
				response.forEach(e =>
				{
					let peer = document.createElement("g-chat-peer");
					peer.hostId = this.hostId;
					peer.peerId = e.id;
					peer.peerName = e.name;
					peer.peerStatus = e.status;
					peer.unread = e.unread;
					div.appendChild(peer);
				});
			}).catch(error => GMessageDialog.error(error.message));
		} else
			Array.from(root.children)
				.forEach(e => div.appendChild(e));
	}

	disconnectedCallback()
	{
		let div = this.shadowRoot.querySelector("div");
		while (div.firstChild)
			div.removeChild(div.firstChild);
	}
});
