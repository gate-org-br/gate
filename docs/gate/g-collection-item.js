let template = document.createElement("template");
template.innerHTML = `
	<slot></slot><button type="button" class="danger"><g-icon>&#x2026;</g-icon></button>
<style data-element="g-collection-item">:host {
	gap: 0.5rem;
	display: grid;
	padding: 0.5rem;
	border-radius: 3px;
	align-items: stretch;
	grid-column: 1 / -1;
	grid-template-columns: 1fr auto;
	border: 1px solid var(--main3, #DDDDDD);
	background-color: var(--main2, #F8F8F8);
}

button {
	grid-column: 2;
	width: 44px;
	color: white;
	height: 100%;
	padding: 8px;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: var(--r2, #AA2222);
}
</style>`;
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

		this.shadowRoot.querySelector("button")
			.addEventListener("click", () =>
				this.dispatchEvent(new CustomEvent("remove", {bubbles: true})));
	}

	set template(elements)
	{
		let btn = this.shadowRoot.querySelector("button");

		Array.from(elements)
			.map(e => e.cloneNode(true))
			.forEach(e => btn.before(e));

		this.#fields = Array.from(this.shadowRoot.querySelectorAll("[name]"));

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
		if (this.#fields.length === 1 && !this.#fields[0].name)
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
		if (this.#fields.length === 1 && !this.#fields[0].name)
		{
			this.#fields[0].value = data ?? null;
			return;
		}

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
