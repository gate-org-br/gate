let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			Chat
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
			<g-chat>
			</g-chat>
		</g-window-section>
	</main>
 <style>:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main
{
	height: auto;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 800px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 500px;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}

g-chat {
	width: 100%;
	height: 492px;
}

g-window-section
{
	padding: 4px;
}</style>`;

/* global customElements, template, GChatContactDialog */

import './g-chat.mjs';
import './g-window-section.mjs';
import GModal from './g-modal.mjs';

export default class GChatDialog extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
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


window.addEventListener("click", event =>
	{
		event = event || window.event;
		let action = event.target || event.srcElement;
		action = action.closest("a[target=_chat]");
		if (action)
		{
			event.preventDefault();
			event.stopPropagation();
			GChatDialog.show(action.getAttribute("data-host-id"),
				action.getAttribute("data-host-name"));
		}
	});