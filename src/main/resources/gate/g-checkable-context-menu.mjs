let template = document.createElement("template");
template.innerHTML = `
	<a href='#'>Inverter seleção<i>&#X2205;</i></a>
	<a href='#'>Selecionar tudo<i>&#X1011;</i></a>
	<a href='#'>Desmarcar tudo<i>&#X1014;</i></a>
`;

/* global customElements, template */

import GContextMenu from './g-context-menu.mjs';

customElements.define('g-checkable-context-menu', class extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();
		this.appendChild(template.content.cloneNode(true));
		this.children[0].addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = !input.checked));
		this.children[1].addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = true));
		this.children[2].addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = false));
	}
});