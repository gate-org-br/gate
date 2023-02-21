let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Contato
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
		</section>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}

g-chat-contact {
	height: 600px;
}</style>`;

/* global customElements, template */

import './g-icon.mjs';
import './g-chat-contact.mjs';
import GWindow from './g-window.mjs';
import Message from './g-message.mjs';
import GChatService from './g-chat-service.mjs';

export default class GChatContactDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
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

	set peerStatus(peerStatus)
	{
		this.setAttribute("peer-status", peerStatus);
	}

	get peerStatus()
	{
		return this.getAttribute("peer-status");
	}

	connectedCallback()
	{
		let contact = document.createElement("g-chat-contact");
		contact.hostId = this.hostId;
		contact.hostName = this.hostName;
		contact.peerId = this.peerId;
		contact.peerName = this.peerName;
		contact.peerStatus = this.peerStatus;
		this.shadowRoot.querySelector("section").appendChild(contact);
	}

	disconnectedCallback()
	{
		this.shadowRoot.querySelector("section").firstElementChild.remove();
	}

	static show(hostId, hostName, peerId, peerName, peerStatus)
	{
		if (hostId && hostName && peerId && peerName && peerStatus)
		{
			let dialog = window.top.document.createElement("g-chat-contact-dialog");
			dialog.hostId = hostId;
			dialog.hostName = hostName;
			dialog.peerId = peerId;
			dialog.peerName = peerName;
			dialog.peerStatus = peerStatus;
			dialog.show();
		}
	}
}

customElements.define('g-chat-contact-dialog', GChatContactDialog);


window.addEventListener("click", event =>
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("a[target=_chat-contact]");
	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		GChatService.peer(action.getAttribute("data-peer-id"))
			.then(response => GChatContactDialog
					.show(action.getAttribute("data-host-id"),
						action.getAttribute("data-host-name"),
						action.getAttribute("data-peer-id"),
						action.getAttribute("data-peer-name"),
						response.status))
			.catch(error => Message.error(error.message));
	}
});