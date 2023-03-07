let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	color: black;
	border: none;
	height: auto;
	flex-grow: 1;
	background-color: #F8F8F8;
	justify-content: flex-start;
}

#container
{
	gap: 8px;
	padding: 8px;
}


::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	gap: 4px;
	padding: 6px;
	height: auto;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	font-size: 12px;
	flex-basis: 140px;
	border-radius: 5px;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	background-color: #F4F4F4;
	justify-content: space-around;
}

:host(.inline) ::slotted(a),
:host(.inline) ::slotted(button),
:host(.inline) ::slotted(.g-command)
{
	min-width: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

::slotted(a[aria-selected]),
::slotted(button[aria-selected]),
::slotted(.g-command[aria-selected])
{
	background-color: #E6E6E6;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color:  #FFFACD;
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: none
}

::slotted(*[hidden="true"])
{
	display: none;
}

::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}
</style>`;

/* global customElements, template */

import GOverflow from "./g-overflow.mjs";

customElements.define('g-tabbar', class extends GOverflow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});