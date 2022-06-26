let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	padding: 4px;
	display: flex;
	cursor: pointer;
	align-items: center;
	justify-content: center;
}



:host([status=enabled])
{
	color: #006600;
}

:host([status=disabled])
{
	color: #666666;
}

:host([status=denied])
{
	color: #660000;
}


:host(*)::before
{
	display: flex;
	font-size: 16px;
	flex-basis: 24px;
	font-family: gate;
	align-items: center;
	justify-content: center;
}

:host([status=enabled])::before
{
	content: "\\2087";
}

:host([status=disabled])::before
{
	content: "\\2089";
}

:host([status=denied])::before
{
	content: "\\2088";
}

:host([label])::after
{
	display: flex;
	align-items: center;
	content: attr(label);
	justify-content: flex-start;
}

</style>`;

/* global customElements */

import GChatNotificatorStatus from './g-chat-notification-status.mjs';

customElements.define('g-chat-notification-config', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", () =>
		{
			switch (Notification.permission)
			{
				case "denied":
					this.status = 'denied';
					GChatNotificatorStatus.set(this.peerId, 'disabled');
					return alert("Solicite à equipe de suporte para efetuar a habilitação das notificações no navegador");
					break;
				case "granted":
					let status = GChatNotificatorStatus.get(this.peerId) || 'disabled';
					status = status === 'disabled' ? 'enabled' : 'disabled';
					GChatNotificatorStatus.set(this.peerId, status);
					this.status = status;
					break;

				default:
					Notification.requestPermission(permission =>
					{
						if (Notification.permission === "granted")
						{
							let status = GChatNotificatorStatus.get(this.peerId) || 'disabled';
							status = status === 'disabled' ? 'enabled' : 'disabled';
							GChatNotificatorStatus.set(this.peerId, status);
							this.status = status;
						} else if (Notification.permission === "denied")
						{
							this.status = 'denied';
							GChatNotificatorStatus.set(this.peerId, 'disabled');
							return alert("Notificações desabilitadas no navegador");
						}
					});
					break;
			}
		});
	}

	connectedCallback()
	{
		if (Notification.permission === "denied")
		{
			this.status = 'denied';
			GChatNotificatorStatus.set(this.peerId, 'disabled');
		} else
			this.status = GChatNotificatorStatus.get(this.peerId);
	}

	get peerId()
	{
		return Number(this.getAttribute("peer-id"));
	}

	set peerId(peerId)
	{
		return this.setAttribute("peer-id", peerId);
	}

	set status(status)
	{
		this.setAttribute("status", status || "disabled");
	}

	get status()
	{
		return this.getAttribute("status") || "disabled";
	}

	attributeChangedCallback()
	{
		switch (this.status)
		{
			case 'enabled':
				this.title = "Notificações habilitadas";
				break;
			case 'disabled':
				this.title = "Notificações desabilitadas";
				break;
			case 'denied':
				this.title = "Notificações desabilitadas no navegador";
				break;
		}
	}

	static get observedAttributes()
	{
		return ['status'];
	}
});
