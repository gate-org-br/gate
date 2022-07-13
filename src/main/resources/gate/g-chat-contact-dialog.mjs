let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			Contato
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
		</g-window-section>
	</main>
 <style>:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main
{
	height: auto;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 600px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 500px;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}

g-window-section
{
	padding: 4px;
}</style>`;

/* global customElements, template */

import './g-chat-contact.mjs';
import './g-window-section.mjs';
import GModal from './g-modal.mjs';
import Message from './g-message.mjs';
import GChatService from './g-chat-service.mjs';

export default class GChatContactDialog extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
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
		this.shadowRoot.querySelector("g-window-section")
			.appendChild(contact);
	}

	disconnectedCallback()
	{
		this.shadowRoot.querySelector("g-window-section")
			.firstElementChild.remove();
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