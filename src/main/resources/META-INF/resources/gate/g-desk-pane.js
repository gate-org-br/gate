let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	gap: 16px;
	width: 100%;
	color: black;
	display: grid;
	font-size: 16px;
	background-color: transparent;
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}

::slotted(a),
::slotted(button),
::slotted(.g-command),
::slotted(g-desk-pane),
::slotted(g-desk-pane-reset)
{
	gap: 10px;
	margin: 0;
	padding: 16px;
	height: 200px;
	color: inherit;
	font-size: inherit;
	position: relative;
	text-align: center;
	border-radius: 3px;
	font-style: normal;
	text-decoration: none;
	border: 1px solid #F0F0F0;

	display: grid;
	align-items: center;
	justify-items: center;
	justify-content: center;
	grid-template-rows: 1fr 1fr;

}

::slotted(:hover)
{
	background-color:  #FFFACD;
}

:host([child])
{
	cursor: pointer;
}

:host([child]) ::slotted(a),
:host([child]) ::slotted(button),
:host([child]) ::slotted(.g-command),
:host([child]) ::slotted(g-desk-pane)
{
	display: none;
}

:host([child])::after
{
	right: 8px;
	bottom: 4px;
	font-family: gate;
	content: '\\3017';
	position: absolute;
	color: var(--main6);
}

::slotted(g-desk-pane-reset)::before
{
	color: #660000;
	font-size: 48px;
	cursor: pointer;
	content: '\\2023';
	font-family: gate;
}

::slotted(g-desk-pane-reset)::after
{
	color: #660000;
	cursor: pointer;
	content: 'Return';
}

:host(.inline)
{
	grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
}

:host(.inline) ::slotted(a),
:host(.inline) ::slotted(button),
:host(.inline) ::slotted(.g-command),
:host(.inline) ::slotted(g-desk-pane),
:host(.inline) ::slotted(g-desk-pane-reset)
{
	height: 80px;
	padding: 16px;
	display: flex;
	align-items: center;
	justify-content: flex-start;
}</style>`;

/* global customElements */

import './g-icon.js';

customElements.define('g-desk-pane', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", function (e)
		{
			let parent = this.parentNode;
			if (parent && parent.tagName === "G-DESK-PANE")
			{
				e.preventDefault();
				e.stopPropagation();

				let backup = parent.buttons;
				this.buttons.forEach(e => parent.appendChild(e));
				backup.forEach(e => e.remove());

				let reset = parent.appendChild(document.createElement("g-desk-pane-reset"));
				reset.addEventListener("click", () =>
				{
					e.preventDefault();
					e.stopPropagation();

					reset.remove();
					parent.buttons.forEach(e => this.appendChild(e));
					backup.forEach(e => parent.appendChild(e));
				});
			}

		}, true);
	}

	get buttons()
	{
		return Array.from(this.children)
			.filter(e => e.tagName === "A"
					|| e.tagName === "BUTTON"
					|| e.tagName === "G-DESK-PANE"
					|| e.classList.contains(".g-command"));
	}

	connectedCallback()
	{
		if (this.parentNode.tagName === "G-DESK-PANE")
			this.setAttribute("child", "");

		this.buttons.flatMap(e => Array.from(e.childNodes))
			.filter(e => e.nodeType === Node.TEXT_NODE)
			.forEach(e => e.parentNode.appendChild(e));

		Array.from(this.querySelectorAll("i, g-icon"))
			.forEach(e => e.style.fontSize = "48px");
		Array.from(this.querySelectorAll("img")).forEach(e =>
		{
			e.style.width = "48px";
			e.style.height = "48px";
		});
	}
});

customElements.define('g-desk-pane-reset', class extends HTMLElement
{
	constructor()
	{
		super();
	}
});