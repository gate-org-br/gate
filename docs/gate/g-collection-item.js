let template = document.createElement("template");
template.innerHTML = `
	<slot></slot><button type="button" class="danger"><g-icon>&#x2026;</g-icon></button>
<style data-element="g-collection-item">:host {
	display: contents;
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
}</style>`;
/* global customElements */
import StyledHTMLElement from './styled-html-element.js';

customElements.define('g-collection-item', class extends StyledHTMLElement
{
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
		elements.forEach(e => btn.before(e.cloneNode(true)));
		this.shadowRoot.querySelector("slot").remove();
		Array.from(this.shadowRoot.querySelectorAll("[name]")).forEach(input =>
		{
			input.addEventListener("change", () =>
				this.dispatchEvent(new CustomEvent("itemchange", {bubbles: true, composed: true})));
			input.addEventListener("input", () =>
				this.dispatchEvent(new CustomEvent("itemchange", {bubbles: true, composed: true})));
		});
	}

	get value()
	{
		return Array.from(this.shadowRoot.querySelectorAll("[name]"))
			.reduce((obj, input) =>
			{
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
		Array.from(this.shadowRoot.querySelectorAll("[name]"))
			.forEach(input =>
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
		return Array.from(this.querySelectorAll("[name]"))
			.every(input => !input.checkValidity || input.checkValidity());
	}

	reportValidity()
	{
		return Array.from(this.querySelectorAll("[name]"))
			.every(input => !input.reportValidity || input.reportValidity());
	}
});