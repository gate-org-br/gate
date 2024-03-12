let template = document.createElement("template");
template.innerHTML = `
	<div>
		<main>
			<label></label>
			<header><g-grid-header></g-grid-header></header>
			<section></section>
			<footer><g-grid-footer></g-grid-footer></footer>
		</main>
	</div>
	<g-callout class='fill'>
		<slot></slot>
	</g-callout>
 <style data-element="g-grid">* {
	box-sizing: border-box;
}

:host(*)
{
	gap: 4px;
	display: flex;
	flex-direction: column;
}

input {
	flex-grow: 1;
}

div {
	display: flex;
	overflow: auto;
	align-items: stretch;
	justify-content: stretch;
}

main
{
	flex-grow: 1;
	display: table;
	border-spacing: 1px;
	background-color: var(--main4);

	table-layout: fixed;
}

label {
	height: 32px;
	color: white;
	line-height: 32px;
	font-weight: bold;
	text-align: center;
	color: var(--base1);
	display: table-caption;
	vertical-align: middle;
	border-radius: 3px 3px 0 0;
	background-color: var(--base3);
}

label:empty {
	display: none;
}

header
{
	display: table-header-group;
}

section
{
	height: 32px;
	cursor: pointer;
	display: table-row-group;
}

footer
{
	display: table-footer-group;
}</style>`;
import './g-callout.js';
import './g-grid-row.js';
import './g-grid-header.js';
import './g-grid-footer.js';

class Column
{
	#index;
	#owner;
	#style;
	#stylesheet;

	constructor(owner, index, stylesheet)
	{
		this.#index = index;
		this.#owner = owner;
		this.#stylesheet = stylesheet;
	}

	get index()
	{
		return this.#index;
	}

	get style()
	{
		if (!this.#style)
		{
			let ruleIndex = this.#stylesheet.insertRule(`g-grid-cell:nth-child(${this.index + 1}) {}`);
			this.#style = this.#stylesheet.cssRules[ruleIndex].style;
		}

		return this.#style;
	}

	get header()
	{
		return this.#owner.shadowRoot.querySelector("g-grid-header").cell(this.index);
	}

	get footer()
	{
		return this.#owner.shadowRoot.querySelector("g-grid-footer").cell(this.index);
	}
}

export default class GGrid extends HTMLElement
{
	#columns;
	#stylesheet;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		this.#columns = new Map();
		this.#stylesheet = new CSSStyleSheet();
		this.shadowRoot.adoptedStyleSheets = [this.#stylesheet];
		this.addEventListener("change", () => this.connectedCallback());
	}

	set caption(value)
	{
		this.shadowRoot.querySelector("label")
			.innerHTML = value || "";
	}

	get columns()
	{
		return Array.from(this.#columns.values());
	}

	set columns(values)
	{
		let header = this.shadowRoot.querySelector("g-grid-header");
		while (header.firstChild)
			header.firstChild.remove();

		values.forEach((header, index) => this.column(index).header.value = header);
	}

	column(index)
	{
		if (!this.#columns.has(index))
			this.#columns.set(index, new Column(this, index, this.#stylesheet));
		return this.#columns.get(index);
	}

	get rows()
	{
		return Array.from(this.shadowRoot.querySelector("section").children);
	}

	row(index = this.length)
	{
		let section = this.shadowRoot.querySelector("section");

		while (section.children.length <= index)
			section.appendChild(document.createElement("g-grid-row"));

		return section.children[index];
	}

	get length()
	{
		return this.shadowRoot.querySelector("section")
			.children.length;
	}

	get movable()
	{
		return this.hasAttribute("movable");
	}

	set movable(value)
	{
		if (value)
			this.setAttribute("movable", "");
		else
			this.removeAttribute("movable");
	}

	set rows(values)
	{
		let section = this.shadowRoot.querySelector("section");
		while (section.firstChild)
			section.firstChild.remove();

		if (values.length)
			if (Array.isArray(values[0]))
			{
				values.forEach(value =>
				{
					let row = this.row();
					row.value = value;
					value.forEach(e => row.cell().value = e);
				});
			} else
			{
				values.forEach(value =>
				{
					let row = this.row();
					row.value = value;
					let keys = Object.keys(value);
					row.cell().value = keys.reduce((val, key) =>
					{
						val[key] = value[key];
						return val;
					}, {});
				});
			}
	}

	set dataset(values)
	{
		this.clear();
		if (values.length)
			if (Array.isArray(values[0]))
			{
				let index = 0;
				values[0].filter(e => !e.startsWith("_"))
					.forEach(header => this.column(index++).header.value = header);

				values.slice(1).forEach(value =>
				{
					let row = this.row();
					row.value = value;

					for (let i = 0; i < values[0].length; i++)
						if (!values[0][i].startsWith("_"))
							row.cell().value = value[i];
				});
			} else
			{
				values.forEach(value =>
				{
					let row = this.row();
					row.value = value;
					let keys = Object.keys(value);
					row.cell().value = keys
						.filter(e => !e.startsWith("_"))
						.reduce((val, key) =>
						{
							val[key] = value[key];
							return val;
						}, {});
				});
			}
	}

	clear()
	{
		this.#columns.clear();
		while (this.#stylesheet.cssRules.length)
			this.#stylesheet.deleteRule(0);

		[this.shadowRoot.querySelector("g-grid-header"),
			this.shadowRoot.querySelector("section"),
			this.shadowRoot.querySelector("g-grid-footer")].forEach(e =>
		{
			while (e.firstChild)
				e.firstChild.remove();
		});
	}

	connectedCallback()
	{
		this.shadowRoot.querySelector("div").style.display = this.length ? "" : "none";
		this.shadowRoot.querySelector("g-callout").style.display = this.length ? "none" : "";
	}
}

customElements.define('g-grid', GGrid);