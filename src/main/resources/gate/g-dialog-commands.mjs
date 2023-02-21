let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	height: 24px;
	color: white;
	outline: none;
	display: flex;
	overflow: hidden;
	border-radius: 5px;
	font-size: inherit;
	align-items: center;
	justify-content: flex-start;
	flex-direction: row-reverse;
}

:host(:empty) {
	display: none
}

::slotted(a),
::slotted(button)
{
	padding: 0;
	width: 24px;
	font-size: 0;
	height: 24px;
	display: flex;
	color: inherit;
	cursor: pointer;
	align-items: center;
	justify-content: center;
}

::slotted(a:hover),
::slotted(button:hover) {
	color: var(--hovered);
}

::slotted(a[hidden="true"]),
::slotted(button[hidden="true"])
{
	display: none;
}</style>`;

/* global customElements */

import GDialog from './g-dialog.mjs';
import GOverflow from "./g-overflow.mjs";

customElements.define('g-dialog-commands', class extends GOverflow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		GDialog.commands = this;
		this.container.style.flexDirection = "row-reverse";
	}
});