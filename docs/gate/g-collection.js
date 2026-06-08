let template = document.createElement("template");
template.innerHTML = `
	<header><slot name="header"></slot></header><button type="button" class="alternative"><g-icon>&#x1002;</g-icon></button><section><slot id="template"></slot></section>
<style data-element="g-collection">* {
	box-sizing: border-box;
}
:host {
	gap: 0.5rem;
	display: grid;
	align-items: start;
	grid-template-columns: 1fr auto;
}

header {
	width: 100%;
	height: 100%;
	display: flex;
	align-items: stretch;
	justify-content: stretch;
}

section {
	display: grid;
	height: 100%;
	gap: 0.5rem;
	grid-row: 2;
	overflow: auto;
	grid-column: span 2;
	align-items: start;
	grid-template-columns: 1fr auto;
}

button {
	grid-column: 2;
	width: 44px;
	color: white;
	height: 100%;
	min-height: 44px;
	padding: 8px;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: var(--g2, #009E60);
}</style>`;
/* global customElements */
import './g-collection-item.js';
import Base64 from "./base64.js";
import GMessageDialog from './g-message-dialog.js';


customElements.define('g-collection', class extends HTMLElement
{
	#template;
	#internals;
	static formAssociated = true;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"})
		this.#internals = this.attachInternals();
		this.shadowRoot.innerHTML += template.innerHTML;

		let slot = this.shadowRoot.getElementById("template");
		slot.addEventListener("slotchange", () =>
		{
			if (slot.parentNode)
			{
				this.#template = slot.assignedElements().map(e => e.cloneNode(true));
				slot.remove();
			}
		});

		this.addEventListener("itemchange", () => this.connectedCallback());

		this.shadowRoot.querySelector("button")
			.addEventListener("click", () => this.add());

		this.shadowRoot.addEventListener("remove", event =>
		{
			event.target.remove();
			this.connectedCallback();
		});
	}

	add(data = null)
	{
		let item = document.createElement("g-collection-item");
		item.template = this.#template;
		if (data)
			item.value = data;
		this.shadowRoot.querySelector("section")
			.appendChild(item);
		this.connectedCallback();
	}

	items()
	{
		return Array.from(this.shadowRoot.querySelectorAll("g-collection-item"));
	}

	get name() { return this.getAttribute("name"); }
	set name(name) { this.setAttribute("name", name); }

	get min() { return this.hasAttribute("min") ? Number(this.getAttribute("min")) : null; }
	set min(min) { this.setAttribute("min", min); }

	get max() { return this.hasAttribute("max") ? Number(this.getAttribute("max")) : null; }
	set max(max) { this.setAttribute("max", max); }

	get value() { return this.items().map(item => item.value); }
	set value(value)
	{
		this.items().forEach(item => item.remove());
		value.forEach(data => this.add(data));
	}

	get required() { return this.hasAttribute("required"); }
	set required(required)
	{
		if (required)
			this.setAttribute("required", "required");
		else
			this.removeAttribute("required");
	}

	get selected()
	{
		let count = this.items().length;
		if (this.min)
			return count >= this.min;
		if (this.required)
			return count > 0;
		return true;
	}

	connectedCallback()
	{
		let items = this.items();
		let count = items.length;
		this.#internals.setFormValue(Base64.encode(JSON.stringify(this.value)));
		this.shadowRoot.querySelector("button").disabled = this.max && count >= this.max;
		if (this.required && !count)
			this.#internals.setValidity({valueMissing: true}, "Adicione ao menos um item");
		else if (this.min && count < this.min)
			this.#internals.setValidity({rangeUnderflow: true}, `Adicione ao menos ${this.min} itens`);
		else if (this.max && count > this.max)
			this.#internals.setValidity({rangeOverflow: true}, `Adicione no máximo ${this.max} itens`);
		else
			this.#internals.setValidity({});
	}

	checkValidity()
	{
		let items = this.items();
		if (this.required && !items.length) return false;
		if (this.min && items.length < this.min) return false;
		if (this.max && items.length > this.max) return false;
		return items.every(item => item.checkValidity());
	}

	reportValidity()
	{
		let items = this.items();
		if (this.required && !items.length)
		{
			GMessageDialog.error("Adicione ao menos um item");
			return false;
		}
		if (this.min && items.length < this.min)
		{
			GMessageDialog.error(`Adicione ao menos ${this.min} itens`);
			return false;
		}
		if (this.max && items.length > this.max)
		{
			GMessageDialog.error(`Adicione no máximo ${this.max} itens`);
			return false;
		}
		return items.every(item => item.reportValidity());
	}

	attributeChangedCallback(attribute)
	{
		if (attribute === "value")
			this.value = JSON.parse(Base64.decode(this.getAttribute("value")));
	}

	static get observedAttributes() { return ['value']; }
});