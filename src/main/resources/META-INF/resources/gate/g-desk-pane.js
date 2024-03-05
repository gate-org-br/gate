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
	grid-auto-rows: 200px;
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


:host(.small)
{
	font-size: 12px;
	grid-auto-rows: 100px;
	grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
}

:host(.small) ::slotted(g-desk-pane-reset)::before
{
	font-size: 32px;
}

:host(.inline)
{
	grid-auto-rows: 80px;
	grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
}

:host(.inline) ::slotted(a),
:host(.inline) ::slotted(button),
:host(.inline) ::slotted(.g-command),
:host(.inline) ::slotted(g-desk-pane),
:host(.inline) ::slotted(g-desk-pane-reset)
{
	display: flex;
	align-items: center;
	justify-content: flex-start;
}

:host(.inline.small)
{
	grid-auto-rows: 40px;
	grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
}


::slotted(:is(a, button, .g-command)[data-loading])
{
	position: relative;
	pointer-events: none;
}

::slotted(:is(a, button, .g-command)[data-loading])::before
{
	top: 0px;
	left: 0px;
	right: 0px;
	bottom: 0px;
	color: black;
	display: flex;
	padding: 32px;
	font-size: 48px;
	content: '\\2017';
	font-family: gate;
	position: absolute;
	border-radius: inherit;
	justify-content: center;
	background-color: #F0F0F0;
}

::slotted(:is(a, button, .g-command)[data-loading])::after
{
	bottom: 48px;
	content: "";
	height: 24px;
	max-width: 80%;
	position: absolute;
	animation-fill-mode: both;
	background-color: var(--base1);
	animation: loading 2s infinite ease-in-out;
}

:host(.small) ::slotted(:is(a, button, .g-command)[data-loading])::before
{
	padding: 16px;
	font-size: 24px;
}


:host(.small) ::slotted(:is(a, button, .g-command)[data-loading])::after
{
	bottom: 24px;
	height: 16px;
}

:host(.inline) ::slotted(:is(a, button, .g-command)[data-loading])::before
{
	padding: 16px;
	font-size: 32px;
	align-items: center;
	justify-content: flex-start;
}

:host(.inline) ::slotted(:is(a, button, .g-command)[data-loading])::after
{
	left: 64px;
	bottom: 28px;
	max-width: calc(100% - 80px);
}

:host(.inline.small) ::slotted(:is(a, button, .g-command)[data-loading])::before
{
	font-size: 24px;
}


:host(.inline.small) ::slotted(:is(a, button, .g-command)[data-loading])::after
{
	left: 48px;
	bottom: 12px;
	max-width: calc(100% - 60px);
}</style>`;

/* global customElements */

import './g-icon.js';
import './loading.js';

document.head.insertAdjacentHTML('beforeend',
	`<style>
		g-desk-pane img {
			order: -1;
			width: 48px;
			height: 48px
		}
		g-desk-pane i, g-desk-pane e, g-desk-pane g-icon {
			order: -1;
			font-size: 48px
		}
		g-desk-pane.small img {
			width: 32px;
			height: 32px
		}
		g-desk-pane.small i, g-desk-pane.small e, g-desk-pane.small g-icon {
			font-size: 24px
		}
	</style>`);

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
	}
});

customElements.define('g-desk-pane-reset', class extends HTMLElement
{
	constructor()
	{
		super();
	}
});