let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Chat
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-chat>
			</g-chat>
		</section>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}

g-chat {
	width: 100%;
	height: 492px;
}</style>`;

/* global customElements, template, GChatContactDialog */

import './g-icon.js';
import './g-chat.js';
import GWindow from './g-window.js';

export default class GChatDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
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

	connectedCallback()
	{
		let contact = this.shadowRoot.querySelector("g-chat");
		contact.hostId = this.hostId;
		contact.hostName = this.hostName;
	}

	static show(hostId, hostName)
	{
		let dialog = window.top.document.createElement("g-chat-dialog");
		dialog.hostId = hostId;
		dialog.hostName = hostName;
		dialog.show();
	}
}

customElements.define('g-chat-dialog', GChatDialog);