let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*) {
	display: flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: center;
}
</style>`;

/* global customElements, template */

import './g-property.mjs';

customElements.define('g-properties', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get value()
	{
		return this.getAtribute("value");
	}

	set value(value)
	{
		Array.from(this.shadowRoot.querySelectorAll("g-property"))
			.forEach(e => e.remove());
		Object.getOwnPropertyNames(value).forEach(e =>
		{
			let property = this.shadowRoot.appendChild(document.createElement("g-property"));
			property.label = e;
			property.value = value[e];
		});
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