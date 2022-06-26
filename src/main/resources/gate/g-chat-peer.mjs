let template = document.createElement("template");
template.innerHTML = `
	<label></label>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	width: 100%;
	padding: 8px;
	height: 40px;
	display: grid;
	cursor: pointer;
	min-height: 40px;
	align-items: center;
	grid-template-columns: 32px 1fr 24px;
}

:host([peer-status='ONLINE'])
{
	order: 1;
}

:host([peer-status='OFFLINE'])
{
	order: 2;
}

:host([peer-status='ONLINE'][unread='0'])
{
	order: 3;
}

:host([peer-status='OFFLINE'][unread='0'])
{
	order: 4;
}

:host([peer-status=ONLINE])
{
	color: #006600;
}

:host([peer-status=OFFLINE])
{
	color: #660000;
}

:host(*)::before
{
	display: flex;
	font-size: 16px;
	font-family: gate;
	align-items: center;
	justify-content: center;
}



:host([peer-status=ONLINE])::before
{
	content: '\\2004';
}

:host([peer-status=OFFLINE])::before
{
	content: '\\2008';
}

:host([unread])::after
{
	width: 24px;
	height: 16px;
	color: white;
	display: flex;
	font-size: 10px;
	font-weight: bold;
	border-radius: 5px;
	align-items: center;
	content: attr(unread);
	justify-content: center;
	background-color: #999999;
}


:host([unread='0'])::after
{
	display: none;
}</style>`;

/* global customElements */

customElements.define('g-chat-peer', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", () =>
		{
			this.getRootNode()
				.host.dispatchEvent(new CustomEvent('selected',
					{detail: {id: this.peerId,
							name: this.peerName,
							status: this.peerStatus}}));
		});

		this._private.ChatEvent = event =>
		{
			let sender = Number(event.detail.sender.id);
			let receiver = Number(event.detail.receiver.id);
			if (receiver === this.hostId
				&& sender === this.peerId)
				this.unread = this.unread + 1;
		};

		this._private.ChatReceivedEvent = event =>
		{
			let sender = Number(event.detail.sender);
			let receiver = Number(event.detail.receiver);
			if (receiver === this.hostId
				&& sender === this.peerId)
				this.unread = 0;
		};

		this._private.LoginEvent = event =>
		{
			let user = Number(event.detail.id);
			if (user === this.peerId)
				this.peerStatus = 'ONLINE';
		};

		this._private.LogoffEvent = event =>
		{
			let user = Number(event.detail.id);
			if (user === this.peerId)
				this.peerStatus = 'OFFLINE';
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
		this.setAttribute("unread", unread || 0);
	}

	get unread()
	{
		return Number(this.getAttribute("unread") || 0);
	}

	connectedCallback()
	{
		window.addEventListener("ChatEvent", this._private.ChatEvent);
		window.addEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
		window.addEventListener("LoginEvent", this._private.LoginEvent);
		window.addEventListener("LogoffEvent", this._private.LogoffEvent);
	}

	attributeChangedCallback()
	{
		this.shadowRoot.querySelector("label").innerHTML = this.peerName;
	}

	disconnectedCallback()
	{
		window.removeEventListener("ChatEvent", this._private.ChatEvent);
		window.removeEventListener("ChatReceivedEvent", this._private.ChatReceivedEvent);
		window.removeEventListener("LoginEvent", this._private.LoginEvent);
		window.removeEventListener("LogoffEvent", this._private.LogoffEvent);
	}

	static get observedAttributes()
	{
		return ['peer-name'];
	}
});
