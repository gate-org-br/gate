let template = document.createElement("template");
template.innerHTML = `
	<section></section><button type="button" class="alternative"><g-icon>&#x1002;</g-icon><span></span></button>
<style data-element="g-collection">* {
	box-sizing: border-box;
}
:host {
	gap: 0;
	display: grid;
	padding: 0.5rem;
	align-items: start;
	border-radius: 3px;
	border: 1px solid var(--main3, #DDDDDD);
	background-color: var(--main1, #FFFFFF);
}

section {
	display: grid;
	height: 100%;
	gap: 0.5rem;
	overflow: auto;
	align-items: start;
	grid-template-columns: 1fr auto;
}

section::after {
	content: attr(data-placeholder);
	padding: 1rem;
	display: none;
	color: var(--main4);
	min-height: 44px;
	border-radius: 3px;
	grid-column: 1 / -1;
	align-items: center;
	justify-content: center;
	border: 1px dashed var(--main3);
}

section[empty]::after {
	display: flex;
}

button {
	gap: 0.5rem;
	width: 100%;
	color: white;
	min-height: 44px;
	margin-top: 0.5rem;
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
}
</style>`;
/* global customElements */
import Base64 from "./base64.js";
import GMessageDialog from './g-message-dialog.js';
import GCollectionItem from './g-collection-item.js';

customElements.define('g-collection', class extends HTMLElement
{
	#internals;

	static formAssociated = true;

	constructor()
	{
		super();

		this.attachShadow({mode: "open"});
		this.#internals = this.attachInternals();
		this.shadowRoot.innerHTML += template.innerHTML;
		if (!this.hasAttribute("tabindex"))
			this.tabIndex = 0;

		this.shadowRoot.querySelector("button")
			.addEventListener("click", () => this.add());

		this.shadowRoot.addEventListener("remove", event =>
		{
			event.target.remove();
			this.update();
			this.dispatchEvent(new Event("change", {bubbles: true}));
		});

		this.shadowRoot.addEventListener("change", event =>
		{
			event.stopPropagation();
			this.update();
			this.dispatchEvent(new Event("change", {bubbles: true}));
		});

		this.shadowRoot.addEventListener("input", event =>
		{
			event.stopPropagation();
			this.update();
			this.dispatchEvent(new Event("input", {bubbles: true}));
		});
	}

	add(data = null)
	{
		const item = GCollectionItem.create(this.template, data);
		this.shadowRoot.querySelector("section").appendChild(item);
		this.update();
		this.dispatchEvent(new Event("change", {bubbles: true}));
	}

	items() { return Array.from(this.shadowRoot.querySelectorAll("g-collection-item")); }

	get template() { return this.querySelector("template")?.content.children ?? []; }

	get name() { return this.getAttribute("name"); }
	set name(name) { this.setAttribute("name", name); }

	get min() { return this.hasAttribute("min") ? Number(this.getAttribute("min")) : null; }
	set min(min) { this.setAttribute("min", min); }

	get max() { return this.hasAttribute("max") ? Number(this.getAttribute("max")) : null; }
	set max(max) { this.setAttribute("max", max); }

	get label() { return this.getAttribute("label"); }
	set label(label)
	{
		if (label == null)
			this.removeAttribute("label");
		else
			this.setAttribute("label", label);
	}

	get placeholder() { return this.getAttribute("placeholder"); }
	set placeholder(placeholder)
	{
		if (placeholder == null)
			this.removeAttribute("placeholder");
		else
			this.setAttribute("placeholder", placeholder);
	}

	get size() { return this.items().length; }

	get entries() { return this.items().map(item => item.value); }

	set entries(entries)
	{
		this.items().forEach(item => item.remove());
		const section = this.shadowRoot.querySelector("section");
		entries.forEach(data => section.appendChild(GCollectionItem.create(this.template, data)));
		this.update();
	}

	get value() { return this.entries.map(entry => Base64.encode(JSON.stringify(entry))).join(";"); }

	set value(value)
	{
		this.entries = (value?.trim() || "")
			.split(/\s*(?:;|\r?\n)\s*/)
			.filter(item => item)
			.map(item => JSON.parse(Base64.decode(item)));
	}

	get required() { return this.hasAttribute("required"); }
	set required(required) { this.toggleAttribute("required", !!required); }

	connectedCallback()
	{
		if (this.hasAttribute("value") && !this.items().length)
			this.value = this.getAttribute("value");
		else
			this.update();
	}

	update()
	{
		let items = this.items();
		let count = items.length;
		let section = this.shadowRoot.querySelector("section");

		this.#internals.setFormValue(this.value);
		section.dataset.placeholder = this.placeholder ?? "";
		section.toggleAttribute("empty", count === 0 && !!this.placeholder);

		let label = this.label;
		let button = this.shadowRoot.querySelector("button");
		let span = button.querySelector("span");
		span.textContent = label ?? "";
		span.hidden = label == null || label === "";

		button.disabled = this.max && count >= this.max;

		if (this.required && !count)
			this.#internals.setValidity({valueMissing: true},
				"Add at least one item", button);
		else if (this.min && count < this.min)
			this.#internals.setValidity({rangeUnderflow: true},
				`Add at least ${this.min} items`, button);
		else if (this.max && count > this.max)
			this.#internals.setValidity({rangeOverflow: true},
				`Add at most ${this.max} items`, button);
		else
			this.#internals.setValidity({});
	}

	checkValidity()
	{
		let items = this.items();

		if (this.required && !items.length)
			return false;

		if (this.min && items.length < this.min)
			return false;

		if (this.max && items.length > this.max)
			return false;

		return items.every(item => item.checkValidity());
	}

	reportValidity()
	{
		let items = this.items();

		if (this.required && !items.length)
		{
			GMessageDialog.error("Add at least one item");
			return false;
		}

		if (this.min && items.length < this.min)
		{
			GMessageDialog.error(`Add at least ${this.min} items`);
			return false;
		}

		if (this.max && items.length > this.max)
		{
			GMessageDialog.error(`Add at most ${this.max} items`);
			return false;
		}

		return items.every(item => item.reportValidity());
	}

	attributeChangedCallback(attribute)
	{
		if (attribute === "value")
		{
			if (this.isConnected)
				this.value = this.getAttribute("value");
		} else
			this.update();
	}

	static get observedAttributes() { return ["value", "label", "placeholder"]; }
});
