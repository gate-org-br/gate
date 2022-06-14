let template = document.createElement("template");
template.innerHTML = `
	<input type="text" placeholder="Localizar"/>
	<div>
	</div>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: grid;
	height: inherit;
	border-radius: 5px;
	align-items: stretch;
	border: 1px solid #CCCCCC;
	grid-template-rows: 32px 1fr;
	background-color: transparent;
}

div
{
	flex-grow: 1;
	display: flex;
	overflow: auto;
	flex-direction: column;
	background-color: white;
	border: 1px solid #CCCCCC;
	border-radius: 0 0 5px 5px;
}

input
{
	border: none;
	border: 1px solid #CCCCCC;
	border-radius: 5px 5px 0 0;
}

g-chat-user:first-child
{
	border-radius: 5px 5px 0 0;
}

g-chat-user:last-child
{
	border-radius:  0 0 5px 5px;
}

g-chat-user:nth-child(even)
{
	background-color: #FFFFFF;
}

g-chat-user:nth-child(odd)
{
	background-color: #EEEEEE;
}

g-chat-user:hover
{
	background-color: var(--hovered);
}</style>`;

/* global customElements */

import './g-chat-user.mjs';
import ChatService from './g-chat-service.mjs';

customElements.define('g-chat-user-list', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let div = this.shadowRoot.querySelector("div");
		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () =>
		{
			let value = input.value.toUpperCase();
			Array.from(div.children)
				.forEach(row => row.style.display = !value
						|| row.name.toUpperCase()
						.indexOf(value.toUpperCase()) !== -1 ? "" : "none");

			let type = "#EEEEEE";
			Array.from(div.children)
				.filter(row => window.getComputedStyle(row).display !== "none")
				.forEach(row =>
				{
					row.style.backgroundColor = type;
					type = type === "#FFFFFF" ? "#EEEEEE" : "#FFFFFF";
				});
		});

		ChatService.users().then(response =>
		{
			response.forEach(e =>
			{
				let user = this.shadowRoot.querySelector("div")
					.appendChild(document.createElement("g-chat-user"));
				user.id = e.id;
				user.name = e.name;
				user.status = e.status;
			});
		});

		this.addEventListener("selected", () =>
		{
			input.value = "";
			Array.from(div.children).forEach(row =>
			{
				row.style.display = "";
				row.style.backgoundColor = "";
			});
		});
	}
});
