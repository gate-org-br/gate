let template = document.createElement("template");
template.innerHTML = `
	<button type="button" class="alternative"><g-icon>&#x1002;</g-icon></button><slot></slot>
<style data-element="g-collection">:host {
	display: grid;
	grid-template-columns: 1fr auto;
	gap: 0.5rem;
	align-items: start;
}

:host::before {
	content: "";

	gap: 12px;
	height: 100%;
	display: flex;
	font-size: 16px;
	padding-left: 8px;
	border: 1px solid;
	text-align: justify;
	align-items: stretch;
	flex-direction: column;
	border-left: 6px solid;
	justify-content: center;
	border-radius: 0 3px 3px 0;
	border-color: var(--main3, #DDDDDD);
	background-color: var(--main1, #FFFFFF);
}

:host([legend])::before { content: attr(legend);}

button {
	grid-column: 2;
	width: 44px;
	color: white;
	height: 44px;
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
import StyledHTMLElement from './styled-html-element.js';


customElements.define('g-collection', class extends StyledHTMLElement
{
	#template;
	#internals;
	static formAssociated = true;

	constructor()
	{
		super();
		this.#internals = this.attachInternals();
		this.shadowRoot.innerHTML += template.innerHTML;

		let slot = this.shadowRoot.querySelector("slot");
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
		this.shadowRoot.appendChild(item);
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