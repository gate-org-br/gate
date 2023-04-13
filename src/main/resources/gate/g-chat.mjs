let template = document.createElement("template");
template.innerHTML = `
	<g-chat-peer-list>
	</g-chat-peer-list>
	<g-chat-contact-list>
	</g-chat-contact-list>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	gap: 8px;
	display: grid;
	align-items: stretch;
	background-color: transparent;
	grid-template-columns: 250px 1fr;
}</style>`;

/* global customElements */

import './g-chat-peer-list.mjs';
import './g-chat-contact-list.mjs';

customElements.define('g-chat', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let contacts = this.shadowRoot.querySelector('g-chat-contact-list');
		this.shadowRoot.querySelector("g-chat-peer-list")
			.addEventListener("selected", e => contacts.add(e.detail));
	}

	get hostId()
	{
		return Number(this.getAttribute("host-id"));
	}

	set hostId(id)
	{
		this.setAttribute("host-id", id);
	}

	get hostName()
	{
		return this.getAttribute("host-name");
	}

	set hostName(hostName)
	{
		this.setAttribute("host-name", hostName);
	}

	select(peer)
	{
		this.shadowRoot.querySelector("g-chat-peer-list").select(peer);
	}
});
