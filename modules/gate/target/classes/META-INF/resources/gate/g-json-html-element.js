let template = document.createElement("template");
template.innerHTML = `
	<section></section>
<style data-element="g-json-html-element">ul {
	margin: 0;
	padding: 8px;
	list-style-type: none;
}

ul ul {
	margin-left: 40px;
}

li {
	padding: 4px;
}

li[data-icon] {
	gap: 8px;
	display: flex;
	align-items: center;
}

li[data-icon]:before {
	font-size: 80%;
	font-family: gate;
	content: attr(data-icon);
}

li[data-action]:hover {
	cursor: pointer;
	background-color: var(--hovered, #FFFACD)
}

dl {
	display: inline-grid;
	grid-template-columns: auto auto;
}

dt {
	margin: 0;
	padding: 4px;
	display: flex;
	font-weight: bold;
	align-items: center;
	justify-content: flex-end;
}

dd {
	margin: 0;
	padding: 4px;
	display: flex;
	align-items: center;
	justify-content: flex-start;
}</style>`;
/* global template */

import Formatter from './formatter.js';

export default class GJsonHTMLElement extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set value(json)
	{
		this.shadowRoot.querySelector("section")
			.innerHTML = Formatter.JSONtoHTML(json);
	}
}

customElements.define('g-json-html-element', GJsonHTMLElement);