let template = document.createElement("template");
template.innerHTML = `
	<a id='copy-text' href='#'>Copiar texto<i>&#X2217;</i></a>
	<a id='copy-link' href='#'>Copiar endereço<i>&#X2159;</i></a>
	<a id='open-link' href='#'>Abrir em nova aba<i>&#X2256;</i></a>
`;

/* global customElements, template */

import Clipboard from './clipboard.mjs';
import GContextMenu from './g-context-menu.mjs';

customElements.define('g-action-context-menu', class extends GContextMenu
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("copy-text").addEventListener("click", () => Clipboard.copy(this.target.innerText, true));
		this.shadowRoot.getElementById("copy-link").addEventListener("click", () => Clipboard.copy(this.context.getAttribute("data-action"), true));
		this.shadowRoot.getElementById("open-link").addEventListener("click", () =>
		{
			let context = this.context;
			let target = this.context.getAttribute("data-target");
			this.context.setAttribute("data-target", "_blank");
			this.context.click();
			this.context.setAttribute("data-target", target);
		});
	}
});


window.addEventListener("contextmenu", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	let menu = document.querySelector("g-action-context-menu")
		|| document.body.appendChild(document.createElement("g-action-context-menu"));
	menu.show(action, event.target, event.clientX, event.clientY);

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});
