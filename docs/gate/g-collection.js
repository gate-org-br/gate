let template = document.createElement("template");
template.innerHTML = `
	<header><label></label><button id="add" type="button" class="alternative"><g-icon>&#x1002;</g-icon></button></header><section></section>
<style data-element="g-collection">* {
	box-sizing: border-box;
}

:host {
	gap: 4px;
	flex-grow: 1;
	display: grid;
	padding: 0.5rem;
	align-items: start;
	align-content: start;
	border-radius: 3px;
	grid-template-columns: 1fr;
	grid-template-rows: auto 1fr;
	border: 1px solid var(--main3, #DDDDDD);
	background-color: var(--main1, #FFFFFF);
}

header {
	width: 100%;
	gap: 0.5rem;
	display: grid;
	align-items: center;
	grid-template-columns: minmax(0, 1fr) auto;
}

label {
	padding: 4px;
	height: 100%;
	display: flex;
	font-weight: 500;
	border-radius: 5px;
	align-items: center;
	background-color: var(--main2);
}

label:empty {visibility: hidden}

section {
	gap: 0.5rem;
	width: 100%;
	display: grid;
	overflow: auto;
	align-content: start;
	grid-template-columns: 1fr;
}

section::after {
	padding: 1rem;
	display: none;
	min-height: 44px;
	border-radius: 3px;
	align-items: center;
	color: var(--fore-b);
	justify-content: center;
	content: attr(data-placeholder);
	border: 1px dashed var(--main3);
}

section:empty::after { display: flex;}

.row {
	gap: 0.5rem;
	width: 100%;
	display: grid;
	align-items: stretch;
	grid-template-columns: minmax(0, 1fr) auto;
}

button {
	width: 44px;
	gap: 0.5rem;
	color: white;
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
	background-color: var(--fore-g, #39511F);
}

.row > button {
	background-color: var(--fore-r, #772E2C);
}</style>`;
/* global customElements */
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

		this.shadowRoot.querySelector("#add")
			.addEventListener("click", () => this.add());

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

	#createRow(data = null)
	{
		let row = document.createElement("div");

		row.className = "row";

		let item = GCollectionItem.create(this.template, data);

		let remove = document.createElement("button");

		remove.type = "button";
		remove.innerHTML = "<g-icon>&#x2026;</g-icon>";

		remove.addEventListener("click", () =>
		{
			row.remove();

			this.update();

			this.dispatchEvent(new Event("change", {bubbles: true}));
		});

		row.append(item, remove);

		return row;
	}

	add(data = null)
	{
		this.shadowRoot.querySelector("section")
			.appendChild(this.#createRow(data));

		this.update();

		this.dispatchEvent(new Event("change", {bubbles: true}));
	}

	items()
	{
		return Array.from(
			this.shadowRoot.querySelectorAll("g-collection-item")
		);
	}

	get template()
	{
		return this.querySelector("template")?.content.children ?? [];
	}

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

	get size()
	{
		return this.items().length;
	}

	get entries()
	{
		return this.items().map(item => item.value);
	}

	set entries(entries)
	{
		let section = this.shadowRoot.querySelector("section");

		section.replaceChildren();

		entries.forEach(data =>
			section.appendChild(this.#createRow(data)));

		this.update();
	}

	get value()
	{
		return this.entries.join("\n");
	}

	set value(value)
	{
		this.entries = (value?.trim() || "")
			.split(/\s*(?:;|\r?\n)\s*/)
			.filter(item => item);
	}

	get required()
	{
		return this.hasAttribute("required");
	}

	set required(required)
	{
		this.toggleAttribute("required", !!required);
	}

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
		let label = this.shadowRoot.querySelector("label");
		let button = this.shadowRoot.querySelector("#add");

		label.textContent = this.label ?? "";

		this.#internals.setFormValue(this.value);

		section.dataset.placeholder = this.placeholder ?? "";

		button.disabled = this.max && count >= this.max;

		if (this.required && !count)
			this.#internals.setValidity(
				{valueMissing: true},
				"Add at least one item",
				button
			);
		else if (this.min && count < this.min)
			this.#internals.setValidity(
				{rangeUnderflow: true},
				`Add at least ${this.min} items`,
				button
			);
		else if (this.max && count > this.max)
			this.#internals.setValidity(
				{rangeOverflow: true},
				`Add at most ${this.max} items`,
				button
			);
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

	static get observedAttributes()
	{
		return ["value", "label", "placeholder"];
	}
});