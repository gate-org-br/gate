let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
	<label></label>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: flex;
	align-items: stretch;
	justify-content: space-between;
}</style>`;

/* global customElements */

import Message from './g-message.mjs';
import GChatService from './g-chat-service.mjs';

customElements.define('g-chat-counter', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this._private = {
			ChatEvent: event =>
			{
				if (Number(event.detail.receiver.id) === this.hostId)
					GChatService.host()
						.then(response => this.value = response.unread)
						.catch(error => Message.error(error.message));
			},

			ChatReceivedEvent: event =>
			{
				if (Number(event.detail.receiver) === this.hostId)
					GChatService.host()
						.then(response => this.value = response.unread)
						.catch(error => Message.error(error.message));
			}
		};
	}

	set value(value)
	{
		this.shadowRoot.querySelector("label").innerHTML =
			value ? `&nbsp(${value})` : '';
	}

	get value()
	{
		return Number(this.shadowRoot.querySelector("label").innerHTML || 0);
	}

	set hostId(hostId)
	{
		this.setAttribute("host-id", hostId);
	}

	get hostId()
	{
		return Number(this.getAttribute("host-id"));
	}

	connectedCallback()
	{
		window.addEventListener("ChatEvent", this._private.ChatEvent);
		window.addEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ChatEvent", this._private.ChatEvent);
		window.removeEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
	}

	attributeChangedCallback()
	{
		GChatService.host()
			.then(response => this.value = response.unread)
			.catch(error => Message.error(error.message));
	}

	static get observedAttributes()
	{
		return ['host-id'];
	}
});