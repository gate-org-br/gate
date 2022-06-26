let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*) {
	cursor: pointer;
}</style>`;

/* global customElements, template */

import './g-window-section.mjs';
import GChatDialog from './g-chat-dialog.mjs';
import GChatService from './g-chat-service.mjs';
customElements.define('g-chat-command', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", event =>
		{
			event.preventDefault();
			event.stopPropagation();
			GChatDialog.show(this.hostId, this.hostName);
		});

		this._private =
			{
				ChatEvent: event =>
				{
					if (this.hostId === Number(event.detail.receiver.id))
						this.unread = this.unread + 1;
				},

				ChatReceivedEvent: event =>
				{
					if (Number(event.detail.receiver) === this.hostId)
					{
						GChatService.summary().then(response =>
						{
							if (response.status === 'success')
								this.unread = response.result.posted;
							else
								Message.error(response.error);
						});
					}
				}
			};
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

	set unread(unread)
	{
		this.setAttribute("unread", unread);
	}

	get unread()
	{
		return Number(this.getAttribute("unread"));
	}

	attributeChangedCallback()
	{
		this.innerHTML = this.unread ?
			`${this._private.innerHTML} (${this.unread})`
			: this._private.innerHTML;
	}

	connectedCallback()
	{
		this.classList.add("g-command");
		this._private.innerHTML = this.innerHTML;
		window.addEventListener("ChatEvent", this._private.ChatEvent);
		window.addEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);

		GChatService.summary().then(response =>
		{
			if (response.status === 'success')
				this.unread = response.result.posted;
			else
				Message.error(response.error);
		});
	}

	disconnectedCallback()
	{
		window.removeEventListener("ChatEvent", this._private.ChatEvent);
		window.removeEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
	}

	static get observedAttributes()
	{
		return ['unread'];
	}
});