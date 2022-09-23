let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	display:flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

label {
	padding: 8px;
	cursor: pointer;
}

label:nth-child(odd)
{
	background-color: #FFFFFF;
}

label:nth-child(even)
{
	background-color: #EEEEEE;
}

label:hover
{
	background-color: var(--hovered);
}</style>`;

/* global customElements, template */

import './g-properties.mjs';
import GTooltip from './g-tooltip.mjs';

customElements.define('g-object-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set value(value)
	{
		this._private.value = value;
		Array.from(this.shadowRoot.querySelectorAll("label")).forEach(e => e.remove());
		value.forEach(e =>
		{
			let label = this.shadowRoot
				.appendChild(document.createElement("label"));
			label.innerHTML = e.label;

			if (e.properties)
			{
				label.addEventListener("mouseleave", () => GTooltip.hide());
				label.addEventListener("mouseenter", () => GTooltip.show(label, "vertical", e.properties));
			}

			label.addEventListener("click", () => this.dispatchEvent(new CustomEvent("selected", {detail: e})));
		});
	}

	get value()
	{
		return this._private.value;
	}

	attributeChangedCallback()
	{
		this.value = JSON.parse(this.getAttribute("value"));
	}

	static get observedAttributes()
	{
		return ["value"];
	}
});