let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: flex;
	height: inherit;
	border-radius: 3px;
	align-items: stretch;
	justify-content: center;
	background: linear-gradient(to bottom, var(--main4) 0, var(--main5) 50%, var(--main4) 100%);
}</style>`;

/* global customElements */

import './g-chat-contact.js';

customElements.define('g-chat-contact-list', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	add(peer)
	{
		let contact = this.shadowRoot.querySelector("g-chat-contact[peer-id='" + peer.id + "']");
		if (!contact)
		{
			contact = document.createElement("g-chat-contact");
			contact.hostId = this.hostId;
			contact.hostName = this.hostName;
			contact.peerId = peer.id;
			contact.peerName = peer.name;
			contact.peerStatus = peer.status;
			this.shadowRoot.appendChild(contact);
		}

		Array.from(this.shadowRoot.querySelectorAll("g-chat-contact")).forEach(e => e.hide());
		contact.show();
	}

	get hostId()
	{
		return this.getRootNode().host.hostId;
	}

	get hostName()
	{
		return this.getRootNode().host.hostName;
	}
});
