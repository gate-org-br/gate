let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<label id='caption'>
				Selecione um ítem
			</label>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<ul is='g-tree-list'></ul>
		</section>
		<footer>
			<g-coolbar>
				<button id='clear' class='primary'>
					Limpar <g-icon>&#X2018;</g-icon>
				</button>
				<hr>
				<button id='cancel' class='tertiary'>
					Cancelar <g-icon>&#X2027;</g-icon>
				</button>
			</g-coolbar>
		</footer>
	</dialog>
 <style>dialog
{
	min-width: 320px;
	max-width: 800px;
	height: fit-content;
	width: calc(100% - 40px);
}

dialog > section
{
	gap: 4px;
	padding: 4px;
	display: flex;
	height: 400px;
	overflow: auto;
	align-items: stretch;
	background-color: white;
}

ul[is='g-tree-list']
{
	margin: 0;
	padding: 4px;
	font-size: 16px;
	list-style-type: none;

	a {
		padding: 4px;
		font-size: inherit;
		align-items: center;
		display: inline-flex;
	}

	a:hover {
		color: blue;
	}

	ul
	{
		display: none;
		list-style-type: none;
	}

	li {
		cursor: pointer;
	}

	li::before
	{
		padding: 4px;
		content: '+';
		color: inherit;
		font-weight: bold;
		font-size: inherit;
		align-items: center;
		display: inline-flex;
		font-family: monospace;
	}

	li[data-expanded]::before {
		content: '-';
	}

	li[data-empty]::before {
		content: ' ';
	}

	li[data-expanded]>ul {
		display: block;
	}
}
</style>`;

/* global customElements, template, fetch */

import './g-icon.js';
import './g-tree-list.js';
import GWindow from './g-window.js';

function tree(picker, options)
{
	return options.map(option =>
	{
		let li = document.createElement("li");
		let anchor = li.appendChild(document.createElement("a"));
		anchor.innerText = option.label;
		anchor.addEventListener("click", () => picker.dispatchEvent(new CustomEvent("commit", {detail: {value: option}})));
		if (option.children && option.children.length)
		{
			let ul = li.appendChild(document.createElement("ul"));
			tree(picker, option.children).forEach(child => ul.appendChild(child));
		}
		return li;
	});
}

export default class GTreePicker extends GWindow
{
	#options;
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("clear").addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: {value: {}}})));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	get options()
	{
		return this.#options || [];
	}

	set options(options)
	{
		this.#options = options;
		let list = this.shadowRoot.querySelector("ul");
		list.innerHTML = "";
		tree(this, options).forEach(li => list.appendChild(li));
	}

	static pick(options, caption)
	{
		if (typeof options === "string")
			return fetch(options)
				.then(response =>
				{
					return response.ok ?
						response.json()
						: response.text().then(message =>
					{
						throw new Error(message);
					});
				}).then(result => GTreePicker.pick(result, caption));

		let picker = window.top.document.createElement("g-tree-picker");
		picker.options = options;
		if (caption)
			picker.caption = caption;
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

customElements.define('g-tree-picker', GTreePicker);