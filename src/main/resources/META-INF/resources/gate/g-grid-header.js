let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-grid-header">* {
	box-sizing: border-box;
}

:host(*)
{
	height: 32px;
	cursor: inherit;
	display: table-row;

}

:host(:empty)
{
	display: none;
}

::slotted(g-grid-cell) {
	top: 0;
	position: sticky;
	font-weight: bold;
	color: var(--base1);
	border-color: var(--base5);
	background-color: var(--base6);
}</style>`;
import './g-grid-cell.js';
import colorize from './colorize.js';

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