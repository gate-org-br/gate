let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*) {
	cursor: pointer
}

i {
	color: inherit;
}</style>`;

/* global customElements, template */

import './g-window-section.mjs';
import GChatService from './g-chat-service.mjs';
import GChatContactDialog from './g-chat-contact-dialog.mjs';
customElements.define('g-chat-contact-command', class extends HTMLElement
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
			GChatContactDialog.show(this.hostId,
				this.hostName,
				this.peerId,
				this.peerName,
				this.peerStatus);
		});

		this._private =
			{
				ChatEvent: event =>
				{
					if (Number(event.detail.receiver.id) === this.hostId
						&& Number(event.detail.sender.id) === this.peerId)
						this.unread = this.unread + 1;
				},

				ChatReceivedEvent: event =>
				{
					if (Number(event.detail.receiver) === this.hostId
						&& Number(event.detail.sender) === this.peerId)
						this.unread = 0;
				},

				LoginEvent: event =>
				{
					if (this.peerId === Number(event.detail.id))
						this.peerStatus = "ONLINE";
				},

				LogoffEvent: event =>
				{
					if (this.peerId === Number(event.detail.id))
						this.peerStatus = "OFFLINE";
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

	set unread(unread)
	{
		this.setAttribute("unread", unread);
	}

	get unread()
	{
		return Number(this.getAttribute("unread"));
	}

	attributeChangedCallback(attribute)
	{
		switch (attribute)
		{
			case 'unread':
				this.innerHTML = this.unread ?
					`${this._private.innerHTML} (${this.unread})`
					: this._private.innerHTML;
				break;

			case 'peer-id':
				if (this.peerId)
					GChatService.count(this.peerId).then(response =>
					{
						if (response.status === 'success')
							this.unread = response.count;
						else
							Message.error(response.error);
					});
				else
					this.unread = 0;
				break;
		}
	}

	connectedCallback()
	{
		this.classList.add("g-command");
		this._private.innerHTML = this.innerHTML;
		window.addEventListener("ChatEvent", this._private.ChatEvent);
		window.addEventListener("LoginEvent", this._private.LoginEvent);
		window.addEventListener("LogoffEvent", this._private.LogoffEvent);
		window.addEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ChatEvent", this._private.ChatEvent);
		window.removeEventListener("LoginEvent", this._private.LoginEvent);
		window.removeEventListener("LogoffEvent", this._private.LogoffEvent);
		window.removeEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
	}

	static get observedAttributes()
	{
		return ['peer-id', 'unread'];
	}
});