let template = document.createElement("template");
template.innerHTML = `
	<input type="checkbox">
	<slot></slot>
 <style>* {
	box-sizing: border-box
}


:host(*)
{
	padding: 8px;
	display: grid;
	overflow: auto;
	overflow-y: auto;
	position: relative;
	border-radius: 3px;
	align-content: start;
	background-color: white;
	grid-template-columns: 24px 1fr;
	align-items: center;
}

:host([columns='1'])  {
	grid-template-columns: 32px 1fr
}
:host([columns='2'])  {
	grid-template-columns: 32px 1fr 32px 1fr
}
:host([columns='3'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='4'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='5'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='6'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='7'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='8'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

*,
::slotted(*)
{

	cursor: pointer;
	border: none !important;
}

input,
::slotted(input)
{
	margin: 0;
	width: 16px;
}

input {
	top: 4px;
	right: 4px;
	position: absolute;
}</style>`;

/* global customElements */

import GContextMenu from './g-context-menu.js';
import GMessageDialog from './g-message-dialog.js';

customElements.define('g-selectn', class extends HTMLElement
{
	#internals;
	static formAssociated = true;

	constructor()
	{
		super();
		this.tabIndex = 0;
		this.attachShadow({mode: "open"});
		this.#internals = this.attachInternals();
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", event =>
		{
			if (event.target.tagName === "LABEL")
				event.target.previousElementSibling.click();
		});

		this.addEventListener("change", () => this.connectedCallback());

		let checker = this.shadowRoot.querySelector("input");
		checker.addEventListener("change", () => Array.from(this.querySelectorAll("input")).forEach(e => e.checked = checker.checked));
	}

	focus()
	{
		super.focus();
		this.querySelector("input").focus();
	}

	set options(options)
	{
		Array.from(this.querySelectorAll("input, label"))
			.forEach(e => e.remove());
		options.forEach(option =>
		{
			let checkbox = this.appendChild(document.createElement("input"));
			checkbox.addEventListener("change", () => this.dispatchEvent(new CustomEvent("change")));
			checkbox.type = "checkbox";
			checkbox.name = this.name;
			checkbox.value = option.value;
			let label = this.appendChild(document.createElement("label"));
			label.innerText = option.label;
		});
	}

	get options()
	{
		return Array.from(this.querySelectorAll("input"))
			.map(checkbox => ({
					"value": checkbox.value,
					"label": checkbox.previousElementSibling.innerText
				}));
	}

	get value()
	{
		return Array.from(this.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.map(checkbox => checkbox.value);
	}

	set value(value)
	{
		Array.from(this.querySelectorAll("input"))
			.forEach(checkbox => checkbox.checked = value.includes(checkbox.value));
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	get required()
	{
		return this.getAttribute("required");
	}

	set required(required)
	{
		if (required)
			this.setAttribute("required", "required");
		else
			this.removeAttribute("required");
	}

	set max(max)
	{
		this.setAttribute("max", "max");
	}

	get max()
	{
		return this.hasAttribute("max") ? Number(this.getAttribute("max")) : null;
	}

	set min(min)
	{
		this.setAttribute("min", "min");
	}

	get min()
	{
		return this.hasAttribute("min") ? Number(this.getAttribute("min")) : null;
	}

	connectedCallback()
	{
		let checked =
			Array.from(this.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.length;

		if (this.required && !checked)
			this.#internals.setValidity({valueMissing: true}, "Selecione ao menos uma opção", this.shadowRoot.querySelector("input"));

		else if (this.min && checked < this.min)
			this.#internals.setValidity({rangeUnderflow: true}, `Selecione ao menos ${this.min} opções`, this.shadowRoot.querySelector("input"));

		else if (this.max && checked > this.max)
			this.#internals.setValidity({rangeOverflow: true}, `Selecione no máximo ${this.max} opções`, this.shadowRoot.querySelector("input"));
		else
			this.#internals.setValidity({});
	}

	attributeChangedCallback(attribute)
	{
		if (attribute === "name")
			Array.from(this.querySelectorAll("input"))
				.forEach(checkbox => checkbox.name = this.name);
		else if (attribute === "value")
			this.value = JSON.parse(this.getAttribute("value"));
		else
			this.options = JSON.parse(this.getAttribute("options"));
	}

	checkValidity()
	{
		let checked =
			Array.from(this.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.length;

		if (this.required && !checked)
			return false;

		if (this.min && checked < min)
			return false;

		if (this.max && checked > max)
			return false;

		return true;
	}

	reportValidity()
	{
		let checked =
			Array.from(this.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.length;

		if (this.required && !checked)
			return false & GMessageDialog.error("Selecione ao menos uma opção");

		if (this.min && checked < min)
			return false & GMessageDialog.error(`Selecione ao menos ${min} opções`);

		if (this.max && checked > max)
			return false & GMessageDialog.error(`Selecione no máximo ${max} opções`);

		return true;
	}

	static get observedAttributes()
	{
		return ['name', 'value', "options"];
	}
});