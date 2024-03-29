let template = document.createElement("template");
template.innerHTML = `
	<header>
		<i></i>
		<label></label>
		<g-chat-notification-config>
		</g-chat-notification-config>
		<input id='search' placeholder="Localizar"/>
	</header>
	<section>
		<g-chat-message-list>
		</g-chat-message-list>
	</section>
	<footer>
		<input id='message' type='text' maxlength='256' placeholder='Mensagem'/>
		<button>&#X2034</button>
	</footer>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: grid;
	flex: 1 1 100%;
	border-radius: 5px;
	align-items: stretch;
	background-color: var(--main3);
	border: 1px solid var(--main4);
	grid-template-rows: 40px 1fr 40px;
}

:host([hidden])
{
	display: none;
}

section
{
	display: flex;
	overflow: auto;
	align-items: stretch;
	background: linear-gradient(to bottom, var(--main4) 0, var(--main5) 50%, var(--main4) 100%);
}

g-chat-message-list
{
	flex-grow: 1;
}

header
{
	padding: 8px;
	display: grid;
	flex-basis: 50px;
	align-items: center;
	border-bottom: 1px solid var(--main6);
	grid-template-columns: 32px 1fr 32px 128px;
}

header > i {
	font-size: 16px;
	flex-basis: 32px;
	font-family: gate;
	font-style: normal;
}

header > label
{
	font-weight: bold;
	border-radius: 5px 0 0 0;
}

:host([status=ONLINE]) > header  > i
{
	color: #006600;
}

:host([status=OFFLINE]) > header > i
{
	color: #660000;
}

footer
{
	display: grid;
	align-items: stretch;
	justify-content: center;
	border-top: 1px solid var(--main6);
	grid-template-columns: 1fr 48px;
}

input
{
	padding: 4px;
	border: none;
	outline: none;
	background-color: var(--hovered);
}

#search
{
	border-radius: 5px;
}

#message
{
	border-radius: 0 0 0 5px;
}

button
{
	border: none;
	cursor: pointer;
	font-family: gate;
	border-radius: 0 0 5px 0;
}</style>`;

/* global customElements */

import './g-chat-message-list.js';
import GChatService from './g-chat-service.js';
import GMessageDialog from './g-message-dialog.js';

customElements.define('g-chat-contact', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let button = this.shadowRoot.querySelector("button");
		let message = this.shadowRoot.getElementById("message");

		let messages = this.shadowRoot.querySelector("g-chat-message-list");

		this._private = {
			ChatEvent: event =>
			{
				let sender = Number(event.detail.sender.id);
				let receiver = Number(event.detail.receiver.id);

				if (sender === this.peerId && receiver === this.hostId)
				{
					if (!this.hasAttribute("hidden"))
					{
						event.detail.status = 'RECEIVED';
						GChatService.received(this.peerId)
							.then(response => {
							}).catch(error =>
						{
							GMessageDialog.error(error.message);
						});
					}

					messages.add(event.detail.date, "REMOTE", event.detail.text, event.detail.status);

				} else if (sender === this.hostId && receiver === this.peerId)
					messages.add(event.detail.date, "LOCAL", event.detail.text, event.detail.status);
			},

			ChatReceivedEvent: event =>
			{
				let sender = Number(event.detail.sender);
				let receiver = Number(event.detail.receiver);
				if (sender === this.hostId
					&& receiver === this.peerId)
					this.shadowRoot.querySelector("g-chat-message-list")
						.update('LOCAL', 'RECEIVED');
			},

			LoginEvent: event =>
			{
				let user = Number(event.detail.id);
				if (user === this.peerId)
					this.peerStatus = 'ONLINE';
			},

			LogoffEvent: event =>
			{
				let user = Number(event.detail.id);
				if (user === this.peerId)
					this.peerStatus = 'OFFLINE';
			}
		};

		const post = () =>
		{
			message.enabled = false;
			GChatService.post(this.peerId, message.value)
				.then(response =>
				{
					message.value = "";
					message.enabled = true;
				})
				.catch(error =>
				{
					GMessageDialog.error(error.message);
					message.enabled = true;
				});
		};
		button.addEventListener("click", () => post());
		message.addEventListener("keydown", event => event.keyCode === 13 && post());


		let search = this.shadowRoot.getElementById("search");
		search.addEventListener("input", () => messages.filter = search.value);

		messages.addEventListener("selected", () => search.value = "");
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
		window.addEventListener("ChatEvent", this._private.ChatEvent);
		window.addEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
		window.addEventListener("LoginEvent", this._private.LoginEvent);
		window.addEventListener("LogoffEvent", this._private.LogoffEvent);

		if (!this.peerName || !this.peerStatus)
		{
			GChatService.peer(this.peerId)
				.then(response =>
				{
					this.peerName = response.name;
					this.peerStatus = response.status;
				}).catch(error => GMessageDialog.error(error.message));
		}

		GChatService.messages(this.peerId).then(response =>
		{
			let messages = this.shadowRoot.querySelector("g-chat-message-list");
			response.forEach(e =>
			{
				if (Number(e.sender.id) === this.hostId)
					messages.add(e.date, "LOCAL", e.text, e.status);
				else if (Number(e.receiver.id) === this.hostId)
					messages.add(e.date, "REMOTE", e.text, e.status);
			});

			GChatService.received(this.peerId)
				.then(response => {
				}).catch(error =>
			{
				GMessageDialog.error(error.message);
			});
		}).catch(error => GMessageDialog.error(error.message));
	}

	disconnectedCallback()
	{
		window.removeEventListener("ChatEvent", this._private.ChatEvent);
		window.removeEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
		window.removeEventListener("LoginEvent", this._private.LoginEvent);
		window.removeEventListener("LogoffEvent", this._private.LogoffEvent);
	}

	show()
	{
		this.removeAttribute("hidden");
		GChatService.received(this.peerId).then(response =>
		{
			this.shadowRoot
				.querySelector("g-chat-message-list")
				.update('REMOTE', 'RECEIVED');
		}).catch(error =>
		{
			GMessageDialog.error(error.message);
		});
	}

	hide()
	{
		this.setAttribute("hidden", "hidden");
	}

	attributeChangedCallback(attribute)
	{
		switch (attribute)
		{
			case 'peer-id':
				this.shadowRoot.querySelector("g-chat-notification-config").peerId = this.peerId;
				break;
			case 'peer-name':
				this.shadowRoot.querySelector("label").innerText = this.peerName;
				break;
			case 'peer-status':
				this.shadowRoot.querySelector("i").innerHTML = this.peerStatus === 'ONLINE' ? '&#X2004' : '&#X2008';
				break;
		}
	}

	static get observedAttributes()
	{
		return ['peer-id', 'peer-name', 'peer-status'];
	}
});
