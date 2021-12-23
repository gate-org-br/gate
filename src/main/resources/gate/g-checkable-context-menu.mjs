let template = document.createElement("template");
template.innerHTML = `
	<a id='invert' href='#'>Inverter seleção<i>&#X2205;</i></a>
	<a id='select' href='#'>Selecionar tudo<i>&#X1011;</i></a>
	<a id='clear' href='#'>Desmarcar tudo<i>&#X1014;</i></a>
`;

/* global customElements, template */

import GContextMenu from './g-context-menu.mjs';

customElements.define('g-checkable-context-menu', class extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("invert").addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = !input.checked));
		this.shadowRoot.getElementById("select").addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = true));
		this.shadowRoot.getElementById("clear").addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = false));
	}
});


window.addEventListener("contextmenu", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("g-selectn");
	if (!action)
		return;

	let menu = document.querySelector("g-checkable-context-menu")
		|| document.body.appendChild(document.createElement("g-checkable-context-menu"));
	menu.show(action, event.target, event.clientX, event.clientY);

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});