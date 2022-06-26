let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: none;
}</style>`;

/* global customElements, template */

import GChatNotificatorStatus from './g-chat-notification-status.mjs';

export default class GChatNotificator extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this._private.ChatEvent = event =>
		{
			let sender = Number(event.detail.sender.id);
			let receiver = Number(event.detail.receiver.id);

			if (receiver === this.hostId
				&& GChatNotificatorStatus.get() === 'enabled'
				&& GChatNotificatorStatus.get(sender) === 'enabled')
				setTimeout(() =>
				{
					if (event.detail.status === 'POSTED')
					{
						let text = `Você recebeu uma mensagem de ${event.detail.sender.name}`;
						let notification = new Notification('Mensagem recebida', {body: text});
						notification.addEventListener("click", () =>
							this.dispatchEvent(new CustomEvent('selected', {detail: event.detail.sender})));
					}
				}, 0);
		};
	}

	get hostId()
	{
		return Number(this.getAttribute("host-id"));
	}

	set hostId(hostId)
	{
		return this.setAttribute("host-id", hostId);
	}

	connectedCallback()
	{
		window.addEventListener("ChatEvent", this._private.ChatEvent);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ChatEvent", this._private.ChatEvent);
	}
}

customElements.define('g-chat-notificator', GChatNotificator);
