let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	height: 38px;
	cursor: inherit;
	display: table-row;
}

:host(.even),
:host(:nth-child(even))
{
	background-color: var(--main1);
}

:host(.odd),
:host(:nth-child(odd))
{
	background-color: var(--main2);
}

:host(:hover){
	background-color: var(--hovered);
}</style>`;

import './g-grid-cell.js';

customElements.define('g-grid-row', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		this.addEventListener("click", e => this.getRootNode().host
				.dispatchEvent(new CustomEvent('select', {detail: this})));

		this.addEventListener("dragover", event => event.preventDefault());
		this.addEventListener("dragstart", event => event.dataTransfer.setData("text/plain", this.index));
		this.addEventListener("drop", event =>
		{
			event.preventDefault();
			let node = this.parentNode.children[Number(event.dataTransfer.getData("text/plain"))];

			let source = node.index;
			let target = this.index;
			this.parentNode.insertBefore(node, this);
			this._private.grid.dispatchEvent(new CustomEvent("move", {detail: {source, target}}));
		});
	}

	get index()
	{
		return [...this.parentNode.children].indexOf(this);
	}

	get length()
	{
		return this.children.length;
	}

	get value()
	{
		return this._private.value;
	}

	set value(value)
	{
		this._private.value = value;
	}

	cell(index = this.length)
	{
		while (this.length <= index)
			this.appendChild(document.createElement("g-grid-cell"));

		return this.children[index];
	}

	connectedCallback()
	{
		this._private.grid = this.getRootNode().host;
		this.draggable = this._private.grid.movable;
		this._private.grid.dispatchEvent(new CustomEvent("change"));
	}

	disconnectedCallback()
	{
		this._private.grid.dispatchEvent(new CustomEvent("change"));
	}
});