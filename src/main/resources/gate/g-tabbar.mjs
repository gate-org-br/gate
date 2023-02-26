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
	display: flex;
	overflow: hidden;
	align-items: stretch;
	justify-content: flex-start;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	margin: 4px;
	padding: 6px;
	height: auto;
	flex-grow: 1;
	display: flex;
	color: inherit;
	flex-shrink: 1;
	flex-basis: 100%;
	max-width: 160px;
	border-radius: 5px;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: space-around;
}

::slotted(a[aria-selected]),
::slotted(button[aria-selected]),
::slotted(.g-command[aria-selected])
{
	background-color: #E8E8E8;
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