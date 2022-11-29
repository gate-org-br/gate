let template = document.createElement("template");
template.innerHTML = `
	<input type="text" placeholder="Filtrar"/>
	<div>
		<table>
		</table>
	</div>
	<label>
		<slot></slot>
	</label>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	gap: 4px;
	display: flex;
	text-align: center;
	align-items: stretch;
	flex-direction: column;
}

input {
	width: 100%;
	height: 32px;
	display: none;
	flex-basis: 32px;
	padding: 8px 4px 8px 4px;
}

:host([filter]) input {
	display: block
}

div {
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: flex-start;
	justify-content: stretch;
}

label {
	display: flex;
	padding: 16px;
	font-size: 24px;
	text-align: center;
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	background-color: #FFFFFF;
}

table {
	margin: 0;
	width: 100%;
	border: none;
	color: black;
	flex-grow: 1;
	position: relative;
	font-weight: normal;
	table-layout: fixed;
	border-spacing: 1px;
	text-decoration: none;
	background-color: #EEEEEE;
}

caption {
	height: 32px;
	color: white;
	line-height: 32px;
	font-weight: bold;
	text-align: center;
	vertical-align: middle;
	border-radius: 5px 5px 0px 0px;
	background-color: var(--table-caption-background-color);
	background-image: var(--table-caption-background-image);
}

table, thead, tbody, tfoot, tr, th, td
{
	color: inherit;
	text-align: inherit;
	font-weight: inherit;
	text-decoration: inherit;
}

tr {
	height: 32px;
	cursor: pointer;
}

tbody>tr:nth-child(even) {
	background-color: var(--table-body-background-color-even);
}

tbody>tr:nth-child(odd) {
	background-color: var(--table-body-background-color-odd);
}

tbody>tr:hover {
	background-color: var(--hovered);
}

td, th {
	padding: 4px;
	vertical-align: middle;
	background-color: transparent;
}

thead > tr
{
	background-color: var(--table-head-background-color);
	background-image: var(--table-head-background-image);
}

thead > tr > th {
	top: 0;
	position: sticky;
	font-weight: bold;
	background-color: var(--table-head-background-color);
	background-image: var(--table-head-background-image);
}

tfoot > tr
{
	background-color: var(--table-foot-background-color);
	background-image: var(--table-foot-background-image);
}

tfoot > tr > td
{
	bottom: 0;
	position: sticky;
}</style>`;

/* global customElements, template, HTMLElement */

import './g-properties.mjs';
import filter from './filter.mjs';
import colorize from './colorize.mjs';

function element(content)
{
	switch (typeof content)
	{
		case "string":
			return document.createTextNode(content);
		case "number":
			return document.createTextNode(content);
		case "undefined":
			return document.createTextNode("");
		case "boolean":
			return document.createTextNode(content ? "Sim" : "Não");
		case "function":
			return element(content());
		case "object":
			if (!content)
				return document.createTextNode("");

			if (content instanceof HTMLElement)
				return content;

			if (Array.isArray(content))
			{
				let ul = document.createElement("ul");
				content.forEach(e => ul.appendChild(document.createElement("li")).appendChild(element(e)));
				return ul;
			}

			let properties = document.createElement("g-properties");
			properties.value = content;
			return properties;
	}
}

customElements.define('g-grid', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		this.shadowRoot.querySelector("label").style.display = "";
		this.shadowRoot.querySelector("div").style.display = "none";

		let input = this.shadowRoot.querySelector("input");
		let table = this.shadowRoot.querySelector("table");
		input.style.display = "none";
		input.addEventListener("input", () =>
		{
			let elements = Array.from(this.shadowRoot.querySelectorAll("tbody > tr"));
			filter(elements, input.value);
			colorize(table);
		});
	}

	set caption(value)
	{
		let caption = this.shadowRoot.querySelector("caption");
		if (value)
		{
			if (!caption)
				caption = this.shadowRoot.querySelector("table")
					.appendChild(document.createElement("caption"));
			caption.innerText = value;
		} else if (caption)
			caption.remove();
	}

	get caption()
	{
		let caption = this.shadowRoot.querySelector("caption");
		return caption ? caption.innerHTML : null;
	}

	set header(value)
	{
		let tr = this.shadowRoot.querySelector("thead > tr");
		if (value)
		{
			if (tr)
				while (tr.firstChild)
					tr.firstChild.remove();
			else
				tr = this.shadowRoot.querySelector("table")
					.appendChild(document.createElement("thead"))
					.appendChild(document.createElement("tr"));
			value.forEach(e => tr.appendChild(document.createElement("th")).innerText = e);
		} else if (tr)
			tr.parentNode.remove();
	}

	set footer(value)
	{
		let tr = this.shadowRoot.querySelector("tfoot > td");
		if (value)
		{
			if (tr)
				while (tr.firstChild)
					tr.firstChild.remove();
			else
				tr = this.shadowRoot.querySelector("table")
					.appendChild(document.createElement("thead"))
					.appendChild(document.createElement("tr"));

			value.forEach(e => tr.appendChild(document.createElement("td")).innerText = e);
		} else if (tr)
			tr.parentNode.remove();
	}

	set values(values)
	{
		let tbody = this.shadowRoot.querySelector("tbody");
		if (values)
		{
			if (tbody)
				while (tbody.firstChild)
					tbody.firstChild.remove();
			else
				tbody = this.shadowRoot.querySelector("table")
					.appendChild(document.createElement("tbody"));

			if (values.length)
			{
				this.shadowRoot.querySelector("div").style.display = "";
				this.shadowRoot.querySelector("input").style.display = "";
				this.shadowRoot.querySelector("label").style.display = "none";
				values.forEach(value =>
				{
					let tr = tbody.appendChild(document.createElement("tr"));

					let content = this.mapper(value);
					if (Array.isArray(content))
						content.forEach(e => tr.appendChild(document.createElement("td")).appendChild(element(e)));
					else
						tr.appendChild(document.createElement("td")).appendChild(element(content));

					tr.addEventListener("click", () => this.dispatchEvent(new CustomEvent('select', {detail: value})));
				});
			} else
			{
				this.shadowRoot.querySelector("label").style.display = "";
				this.shadowRoot.querySelector("div").style.display = "none";
				this.shadowRoot.querySelector("input").style.display = "none";
			}
		} else if (tbody)
		{
			tbody.remove();
			this.shadowRoot.querySelector("label").style.display = "";
			this.shadowRoot.querySelector("div").style.display = "none";
			this.shadowRoot.querySelector("input").style.display = "none";
		}
	}

	set mapper(mapper)
	{
		this._private.mapper = mapper || (e => e);
	}

	get mapper()
	{
		return this._private.mapper || (e => e);
	}
});