let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	height: 32px;
	cursor: inherit;
	display: table-row;
	background-color: var(--table-head-background-color);
	background-image: var(--table-head-background-image);
}

:host(:empty)
{
	display: none;
}

::slotted(g-grid-cell) {
	top: 0;
	position: sticky;
	font-weight: bold;
	background-color: var(--table-head-background-color);
	background-image: var(--table-head-background-image);
}</style>`;

import './g-grid-cell.mjs';
import colorize from './colorize.mjs';

customElements.define('g-grid-header', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
	}

	get index()
	{
		return [...this.parentNode.children].indexOf(this);
	}

	get length()
	{
		return this.children.length;
	}

	cell(index = this.length)
	{
		while (this.length <= index)
			this.appendChild(document.createElement("g-grid-cell"));

		return this.children[index];
	}
});