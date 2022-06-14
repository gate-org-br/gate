let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	height: auto;
	display: flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: flex-start;
}</style>`;

/* global customElements */

import './g-chat-message.mjs';
import ChatService from './g-chat-service.mjs';

customElements.define('g-chat-message-list', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("selected", e =>
		{
			this.filter = "";
			e.detail.scrollIntoView();
		});

		window.addEventListener("ChatEvent", event =>
		{
			let sender = Number(event.detail.sender.id);
			let receiver = Number(event.detail.receiver.id);
			if (sender === this.hostId && receiver === this.peerId)
				this.add(event.detail.date, "LOCAL", event.detail.text);
			else if (sender === this.peerId && receiver === this.hostId)
				this.add(event.detail.date, "REMOTE", event.detail.text);
		});
	}

	add(date, type, message)
	{
		let element = document.createElement("g-chat-message");
		element.date = date;
		element.type = type;
		element.text = message;
		this.shadowRoot.appendChild(element);
		element.scrollIntoView();
	}

	connectedCallback()
	{
		ChatService.messages(this.peerId)
			.then(response =>
			{
				response.forEach(e =>
				{
					if (Number(e.sender.id) === this.hostId)
						this.add(e.date, "LOCAL", e.text);
					else if (Number(e.receiver.id) === this.hostId)
						this.add(e.date, "REMOTE", e.text);
				});
			});
	}

	get filter()
	{
		return this.getAttribute("filter");
	}

	set filter(filter)
	{
		this.setAttribute("filter", filter);
	}

	get hostId()
	{
		return this.getRootNode().host.hostId;
	}

	get hostName()
	{
		return this.getRootNode().host.hostName;
	}

	get peerId()
	{
		return this.getRootNode().host.peerId;
	}

	get peerName()
	{
		return this.getRootNode().host.peerName;
	}
	get status()
	{
		return this.getRootNode().host.status;
	}

	attributeChangedCallback()
	{
		let value = this.getAttribute("filter");
		Array.from(this.shadowRoot.querySelectorAll("g-chat-message"))
			.forEach(message =>
				message.style.display = !value
					|| message.text.toUpperCase()
					.indexOf(value.toUpperCase()) !== -1 ? "" : "none");
	}

	static get observedAttributes()
	{
		return ['filter'];
	}
});