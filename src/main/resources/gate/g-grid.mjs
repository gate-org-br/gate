let template = document.createElement("template");
template.innerHTML = `
	<nav>
		<input id="input" type="text" placeholder="Filtrar"/>
	</nav>
	<div>
		<main>
			<label></label>
			<header><g-grid-header></g-grid-header></header>
			<section></section>
			<footer><g-grid-footer></g-grid-footer></footer>
		</main>
	</div>
	<aside>
		<slot></slot>
	</aside>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	gap: 4px;
	display: flex;
	flex-direction: column;
}

nav {
	height: 32px;
	display: none;
	min-height: 32px;
	align-items: stretch;
	justify-content: stretch;
}

:host([filter]) nav
{
	display: flex;
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
	background-color: #EEEEEE;

	table-layout: fixed;
}

label {
	height: 32px;
	color: white;
	line-height: 32px;
	font-weight: bold;
	text-align: center;
	display: table-caption;
	vertical-align: middle;
	border-radius: 5px 5px 0px 0px;
	background-color: var(--table-caption-background-color);
	background-image: var(--table-caption-background-image);
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

aside {
	padding: 12px;
	display: flex;
	font-size: 20px;
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	background-color: white;
}

footer
{
	display: table-footer-group;
}</style>`;

import './g-grid-row.mjs';
import './g-grid-header.mjs';
import './g-grid-footer.mjs';
import filter from './filter.mjs';
import colorize from './colorize.mjs';

class Column
{
	constructor(owner, index)
	{
		this._private = {};
		this._private.index = index;
		this._private.owner = owner;
	}

	get index()
	{
		return this._private.index;
	}

	get style()
	{
		if (!this._private.style)
		{
			let sheet = this._private.owner._private.stylesheet;
			let ruleIndex = sheet.insertRule(`g-grid-cell:nth-child(${this.index + 1}) {}`);
			this._private.style = sheet.cssRules[ruleIndex].style;
		}

		return this._private.style;
	}

	get header()
	{
		return this._private.owner.shadowRoot.querySelector("g-grid-header").cell(this.index);
	}

	get footer()
	{
		return this._private.owner.shadowRoot.querySelector("g-grid-footer").cell(this.index);
	}
}

customElements.define('g-grid', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		this._private = {};
		this._private.columns = new Map();
		this._private.stylesheet = new CSSStyleSheet();

		this.shadowRoot.adoptedStyleSheets = [this._private.stylesheet];

		this.addEventListener("change", () => this.connectedCallback());

		let input = this.shadowRoot.getElementById("input");
		let section = this.shadowRoot.querySelector("section");
		input.addEventListener("input", () =>
		{
			let rows = this.rows;
			filter(rows, input.value);
			colorize(rows);
		});
	}

	set caption(value)
	{
		this.shadowRoot.querySelector("label")
			.innerHTML = value || "";
	}

	get columns()
	{
		return Array.from(this._private.columns.values());
	}

	column(index)
	{
		if (!this._private.columns.has(index))
			this._private.columns.set(index, new Column(this, index));
		return this._private.columns.get(index);
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

	set dataset(values)
	{
		this.clear();
		if (values.length)
			if (Array.isArray(values[0]))
			{
				values[0].slice(1).forEach((header, index) =>
					this.column(index).header.value = header);

				values.slice(1).forEach(value =>
				{
					let row = this.row();
					row.value = value;
					value.slice(1).forEach(e => row.cell().value = e);
				});
			} else
			{
				values.forEach(value =>
				{
					let row = this.row();
					row.value = value;

					let keys = Object.keys(value);
					if (keys.length === 2
						&& keys.includes("label")
						&& keys.includes("value"))
						row.cell().value = value.label;
					else if (keys.length === 3
						&& keys.includes("label")
						&& keys.includes("value")
						&& keys.includes("properties"))
						row.cell().value = value.properties;
					else
						row.cell().value = keys.slice(1).reduce((val, key) =>
						{
							val[key] = value[key];
							return val;
						}, {});
				});
			}
	}

	clear()
	{
		this._private.columns.clear();
		while (this._private.stylesheet.cssRules.length)
			this._private.stylesheet.deleteRule(0);

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
		if (this.length)
		{
			this.shadowRoot.querySelector("div").style.display = "";
			this.shadowRoot.querySelector("nav").style.display = "";
			this.shadowRoot.querySelector("aside").style.display = "none";
		} else
		{
			this.shadowRoot.querySelector("nav").style.display = "none";
			this.shadowRoot.querySelector("div").style.display = "none";
			this.shadowRoot.querySelector("aside").style.display = "";
		}
	}
});