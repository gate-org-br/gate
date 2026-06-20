let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
<style data-element="g-collection-item">:host {
	display: block;
}</style>`;
import StyledHTMLElement from './styled-html-element.js';

export default class GCollectionItem extends StyledHTMLElement
{
	#fields = [];

	static create(template, data = null)
	{
		let item = document.createElement("g-collection-item");

		item.template = template;

		if (data !== null)
			item.value = data;

		return item;
	}

	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set template(elements)
	{
		Array.from(elements)
			.map(e => e.cloneNode(true))
			.forEach(e => this.shadowRoot.append(e));

		this.#fields = Array.from(this.shadowRoot.querySelectorAll("*"))
			.filter(e =>
				e.matches("input, select, textarea")
				|| customElements.get(e.localName)?.formAssociated === true);

		this.shadowRoot.querySelector("slot")?.remove();

		this.#fields.forEach(input =>
		{
			input.addEventListener("change", () =>
				this.dispatchEvent(new Event("change", {bubbles: true, composed: true})));

			input.addEventListener("input", () =>
				this.dispatchEvent(new Event("input", {bubbles: true, composed: true})));
		});
	}

	get value()
	{
		if (this.#fields.length === 1
			&& !this.#fields[0].name)
			return this.#fields[0].value;

		return this.#fields.reduce((obj, input) =>
		{
			if (!input.name)
				throw new Error("Missing input name");

			if (input.tagName === "INPUT"
				&& ["checkbox", "radio"].includes(input.type))
			{
				if (input.checked)
				{
					if (obj[input.name] === undefined)
						obj[input.name] = input.value;
					else if (Array.isArray(obj[input.name]))
						obj[input.name].push(input.value);
					else
						obj[input.name] = [obj[input.name], input.value];
				}
			} else
				obj[input.name] = input.value;

			return obj;
		}, {});
	}

	set value(data)
	{
		this.#fields.forEach(input =>
		{
			let value = data[input.name];

			if (input.tagName === "INPUT"
				&& ["checkbox", "radio"].includes(input.type))
			{
				let values = value == null ? [] : Array.isArray(value) ? value : [value];
				input.checked = values.map(String).includes(input.value);
			} else
				input.value = value ?? null;
		});
	}

	checkValidity()
	{
		return this.#fields
			.every(input => !input.checkValidity || input.checkValidity());
	}

	reportValidity()
	{
		return this.#fields
			.every(input => !input.reportValidity || input.reportValidity());
	}
}

customElements.define('g-collection-item', GCollectionItem);