let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*) {
	display: none
}</style>`;

/* global customElements */

import GDialog from './g-dialog.mjs';

customElements.define('g-dialog-caption', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	connectedCallback()
	{
		GDialog.caption = this.innerText;
	}
});