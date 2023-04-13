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

import  './g-chat-message.mjs';
import GChatService from './g-chat-service.mjs';
import GMessageDialog from './g-message-dialog.mjs';

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
	}

	add(date, type, message, status)
	{
		let element = document.createElement("g-chat-message");
		element.date = date;
		element.type = type;
		element.text = message;
		element.status = status;
		this.shadowRoot.appendChild(element);
		element.scrollIntoView();
	}

	update(type, status)
	{
		Array.from(this.shadowRoot.children)
			.filter(e => e.type === type)
			.forEach(e => e.status = status);
	}

	get filter()
	{
		return this.getAttribute("filter");
	}

	set filter(filter)
	{
		this.setAttribute("filter", filter);
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