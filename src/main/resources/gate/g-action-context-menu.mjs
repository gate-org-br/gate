let template = document.createElement("template");
template.innerHTML = `
	<a href='#'>Copiar texto<i>&#X2217;</i></a>
	<a href='#'>Copiar endereço<i>&#X2159;</i></a>
	<a href='#'>Abrir em nova aba<i>&#X2256;</i></a>
`;

/* global customElements, template */

import Clipboard from './clipboard.mjs';
import GContextMenu from './g-context-menu.mjs';

customElements.define('g-action-context-menu', class extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();
		this.appendChild(template.content.cloneNode(true));
		this.children[0].addEventListener("click", () => Clipboard.copy(this.target.innerText, true));
		this.children[1].addEventListener("click", () => Clipboard.copy(this.context.getAttribute("data-action"), true));
		this.children[2].addEventListener("click", () =>
		{
			let context = this.context;
			let target = this.context.getAttribute("data-target");
			this.context.setAttribute("data-target", "_blank");
			this.context.click();
			this.context.setAttribute("data-target", target);
		});
	}
});