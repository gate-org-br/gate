let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
<style data-element="g-select1">* {
	box-sizing: border-box
}

:host(*) {
	padding: 8px;
	display: grid;
	overflow: auto;
	overflow-y: auto;
	position: relative;
	border-radius: 3px;
	align-items: center;
	grid-auto-rows: 24px;
	align-content: start;
	background-color: var(--main1, white);
	grid-template-columns: 24px 1fr;
}

:host([columns='1']) {
	grid-template-columns: 32px 1fr
}

:host([columns='2']) {
	grid-template-columns: 32px 1fr 32px 1fr
}

:host([columns='3']) {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr
}

:host([columns='4']) {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

:host([columns='5']) {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

:host([columns='6']) {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

:host([columns='7']) {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

:host([columns='8']) {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

*,
::slotted(*) {

	cursor: pointer;
	border: none !important;
}

input,
::slotted(input) {
	margin: 0;
	width: 16px;
}

input {
	top: 4px;
	right: 4px;
	position: absolute;
}</style>`;
/* global customElements */
import GMessageDialog from './g-message-dialog.js';

customElements.define('g-select1', class extends HTMLElement
{
	#options;
	#internals;
	static formAssociated = true;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.#internals = this.attachInternals();
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", event =>
		{
			if (event.target.tagName === "LABEL")
				event.target.previousElementSibling.click();
		});

		this.addEventListener("change", () => this.connectedCallback());
	}

	focus()
	{
		super.focus();
		this.querySelector("input").focus();
	}

	set options(options)
	{
		this.#options = options;
		Array.from(this.querySelectorAll("input, label")).forEach(e => e.remove());
		options.forEach(option =>
		{
			let cb = this.appendChild(document.createElement("input"));
			cb.addEventListener("change", () => this.dispatchEvent(new CustomEvent("change")));
			cb.type = "radio";
			cb.name = this.name;
			cb.value = option.value;
			let label = this.appendChild(document.createElement("label"));
			label.innerText = option.label;
		});
	}

	get options()
	{
		return this.#options;
	}

	get value()
	{
		let checked = Array.from(this.querySelectorAll("input")).find(cb => cb.checked);
		return checked ? checked.value : null;
	}

	set value(value)
	{
		Array.from(this.querySelectorAll("input"))
			.forEach(cb => cb.checked = cb.value === value);
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

	get selected()
	{
		return Array.from(this.querySelectorAll("input"))
			.some(cb => cb.checked);
	}

	connectedCallback()
	{
		this.#internals.setFormValue(this.value);
		if (this.required && !this.selected)
			this.#internals.setValidity(
				{valueMissing: true},
				"Selecione ao menos uma opção",
				this.shadowRoot.querySelector("input"));
		else
			this.#internals.setValidity({});
	}

	attributeChangedCallback(attribute)
	{
		if (attribute === "name")
			Array.from(this.querySelectorAll("input"))
				.forEach(cb => cb.name = this.name);
		else if (attribute === "value")
			this.value = this.getAttribute("value");
		else
			this.options = JSON.parse(this.getAttribute("options"));
	}

	checkValidity() { return !this.required || this.selected; }

	reportValidity()
	{
		if (this.required && !this.selected)
		{
			GMessageDialog.error("Selecione ao menos uma opção");
			return false;
		}
		return true;
	}

	static get observedAttributes() { return ['name', 'value', 'options']; }
});